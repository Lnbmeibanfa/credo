## Context

当前登录依赖微信 `getPhoneNumber` + `POST /api/auth/mini/phone-login`。个人主体正式小程序真机无手机号快速验证权限，开发者工具可模拟成功但真机失败。`user_wechat_bind.open_id` 已存在且唯一；契约/账本只依赖 `userId`。用户确认采用 openid 静默换票方案：phone 可空、废弃 phone-login、欢迎页点按钮触发登录、本期不绑手机号。

## Goals / Non-Goals

**Goals:**

- 真机/体验版可通过 `wx.login` 完成登录并获得 JWT
- 登录身份锚点为 `openid`（经 `user_wechat_bind`）
- `user_account.phone` 可空，为未来绑手机号留空间
- 欢迎页展示明确「微信登录」CTA，点击后再换票（可控、可重试）
- 已有 bind 的用户续登保留原 `userId` 与业务数据

**Non-Goals:**

- 手机号绑定、短信、`getPhoneNumber` 双轨
- 进首页自动登录
- 修改契约/账本表或业务 API
- 清洗无 bind 的历史 phone 用户

## Decisions

### 1. Phone 列可空（非占位）

**选择：** Flyway `V5__user_account_phone_nullable.sql`：`MODIFY phone VARCHAR(20) NULL`，保留 `uk_phone`。

**理由：** MySQL UNIQUE 允许多个 NULL；语义清晰；避免 `wx_xxx` 脏数据。

**备选：** 占位 phone — 零迁移但污染数据，弃用。

**无手机号用户：** `country_code` 保持默认 `'86'`。

### 2. 新 API，废弃 phone-login

**选择：** `POST /api/auth/mini/wechat-login`，body：`{ "loginCode": "..." }`。

**流程：**

1. `code2Session(loginCode)` → openid / unionid
2. 按 openid 查 `user_wechat_bind`
3. 有 bind → 加载 user；校验 status；更新 `last_login_at`
4. 无 bind → `INSERT user_account(phone=NULL, ...)` + `INSERT bind`
5. 签发 JWT（subject=userId；phone claim 可为 null/省略）

**选择：** 删除或不再暴露 `phone-login` 端点（实现时移除 Controller 映射及相关前端调用）。

**备选：** 双轨保留 — 增加维护成本且个人主体真机用不上，弃用。

### 3. 欢迎页：按钮触发（可控）

**选择：** 未登录显示「微信登录」按钮；`onClick` → `Taro.login` → `wechatLogin()` → 存 token → 刷新为「开始立约」。

**理由：** 用户明确要可控；失败时可再次点击；避免 `useDidShow` 自动登录与 token 校验竞态复杂化。

**备选：** 进页自动 login — 更「静默」，但失败与重复请求难感知。

### 4. JWT / UserDto

**选择：** `generateToken(userId, phone)` 允许 `phone == null`；`UserDto.phone` 可为 null。前端不依赖展示手机号登录。

**鉴权：** 继续只解析 userId；契约/账本无变更。

### 5. WeChatClient.getPhoneNumber

**选择：** 登录路径不再调用；客户端方法本 change 可不删（死代码可后续清理），避免无关重构。

## Risks / Trade-offs

- **[Risk] 已发体验版仍调 phone-login** → 必须同步发版小程序与后端；旧客户端会 404/失败
- **[Risk] loginCode 一次性且短时有效** → 前端每次点击重新 `wx.login`；失败提示重试
- **[Risk] 无手机号弱身份** → 接受为个人主体 MVP；Non-goal 绑号
- **[Risk] 同一人清缓存后换微信** → 新 openid 新用户；可接受

## Migration Plan

1. 先部署后端（含 V5 + wechat-login）；短暂期内旧 phone-login 可先保留再删，或一次性切换（推荐与小程序同发）
2. 小程序改为 wechat-login 后上传体验版
3. 回滚：还原迁移需谨慎（NULL phone 行需先补值才能改回 NOT NULL）— 回滚优先保留可空列、恢复旧代码路径仅作紧急手段

## Open Questions

（无 — 已确认：phone 可空、废弃 phone-login、按钮登录、默认续登与不绑号）
