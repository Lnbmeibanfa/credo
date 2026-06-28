## Context

当前 `user_account` 表（V1）以 `phone` 为唯一键，无微信绑定字段。欢迎页（welcome-screen）CTA 直接跳转 contract-create，无登录 gate。

用户确认决策：
1. **方案 B**：`user_account` 主表 + `user_wechat_bind` 绑定表
2. **欢迎页授权**：在欢迎页完成手机号登录后再允许「开始立约」
3. **强制 wx.login**：phone-login 必须同时提交 loginCode 与 phoneCode
4. **AppID**：`wxbda744f66076ee8e`

## Goals / Non-Goals

**Goals:**

- V2 Flyway migration 扩展主表并创建微信绑定表
- 手机号存在则登录，不存在则注册，均绑定 openid
- 返回 JWT，小程序本地存储 token
- 欢迎页：未登录显示授权流程，已登录可点 CTA

**Non-Goals:**

- Web SMS 登录
- 修改 V1 migration
- Mock 登录长期替代方案（开发可临时 mock，但 spec 以真实 API 为准）

## Decisions

### 1. 表结构：方案 B

**user_account**（主表，跨端身份）保留 phone 唯一；扩展 country_code、last_login_at。

**user_wechat_bind**（渠道绑定）存储 openid（唯一）、union_id（可空）、app_id。

Flyway 脚本见下方 `V2__extend_user_account_and_wechat_bind.sql`。

**理由**：phone 作为 Web/小程序统一主键；微信字段隔离，避免主表膨胀。

### 2. 登录 API 契约

```
POST /api/auth/mini/phone-login
Content-Type: application/json

{
  "loginCode": "wx.login() 的 code",
  "phoneCode": "getPhoneNumber 的 code"
}

→ 200 { token, user, isNewUser }
```

**流程**：

1. `code2Session(loginCode)` → openid, unionid(optional)
2. `getPhoneNumber(phoneCode)` → purePhoneNumber, countryCode
3. `SELECT user_account WHERE phone = ?` → 无则 INSERT，有则校验 status
4. `UPSERT user_wechat_bind` by open_id
5. 更新 last_login_at，签发 JWT

### 3. 欢迎页交互

- 页面加载：若本地无 token，显示「授权手机号登录」PrimaryButton（openType=getPhoneNumber）
- 授权成功 + API 返回 token → 存储 token，显示「开始立约」CTA
- 已有 token：直接显示 CTA（可选：启动时 silent validate，MVP 仅检查 localStorage）

### 4. 强制双 code

Controller 校验 loginCode 与 phoneCode 均非空；缺一返回 400 `INVALID_PARAMETER`。

**理由**：openid 绑定是硬性要求，不能仅手机号登录。

### 5. AppID 配置

```
WECHAT_MINI_APP_ID=wxbda744f66076ee8e
WECHAT_MINI_APP_SECRET=<env>
```

`user_wechat_bind.app_id` 写入配置中的 appId。小程序 `project.config.json` 同步 appid。

### 6. JWT Payload

```json
{ "sub": "<userId>", "phone": "<phone>" }
```

expire 使用现有 `jwt.expire-hours`（168h）。

### 7. 测试策略（Pragmatic TDD）

AuthService 单元测试（Mock WeChatClient）：
- 新用户注册成功
- 老用户登录成功
- phoneCode 无效
- loginCode 无效
- 用户 status=0 禁用
- 重复 phone 幂等绑定 openid

Controller `@WebMvcTest` 契约测试。

## Flyway V2 SQL（设计稿）

```sql
-- V2__extend_user_account_and_wechat_bind.sql

ALTER TABLE user_account
    ADD COLUMN country_code VARCHAR(8) NOT NULL DEFAULT '86' COMMENT '国家区号' AFTER phone,
    ADD COLUMN last_login_at DATETIME DEFAULT NULL COMMENT '最后登录时间';

CREATE TABLE user_wechat_bind (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '绑定ID',

    user_id BIGINT NOT NULL COMMENT '用户ID',

    app_id VARCHAR(32) NOT NULL COMMENT '小程序 AppID',

    open_id VARCHAR(64) NOT NULL COMMENT '微信 openid',

    union_id VARCHAR(64) DEFAULT NULL COMMENT '微信 unionid',

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_open_id (open_id),
    KEY idx_user_id (user_id),
    KEY idx_union_id (union_id),
    CONSTRAINT fk_wechat_bind_user FOREIGN KEY (user_id) REFERENCES user_account(id)
) COMMENT='用户微信绑定表';
```

## Backend Package Layout

```
com.credo.credo_server/
├── controller/AuthController.java
├── service/AuthService.java
├── service/JwtService.java
├── client/WeChatClient.java
├── entity/UserAccount.java
├── entity/UserWechatBind.java
├── mapper/UserAccountMapper.java
├── mapper/UserWechatBindMapper.java
├── dto/auth/PhoneLoginRequest.java
├── dto/auth/PhoneLoginResponse.java
├── common/ApiResponse.java
├── common/ErrorCode.java
└── config/WeChatProperties.java
```

## Mini Flow

```
欢迎页 onLoad
  ├─ token 存在 → 显示 CTA
  └─ token 不存在 → 显示「授权登录」Button(openType=getPhoneNumber)
        │
        onGetPhoneNumber + 并行 wx.login
        │
        authService.phoneLogin({ loginCode, phoneCode })
        │
        存 token → 显示 CTA
```

## Risks / Trade-offs

| 风险 | 缓解 |
|------|------|
| 手机号能力需认证+付费 | 开发阶段用真实 AppID；集成测试 mock WeChatClient |
| getPhoneNumber code 5min 过期 | 前端拿到后立即请求后端 |
| 仅 uk_phone 不含 country_code | MVP 默认 86；国际化时改 uk(country_code, phone) |
| V1 已有数据 | V2 ALTER 兼容，country_code 默认 86 |

## Open Questions

（已全部确认，无遗留）
