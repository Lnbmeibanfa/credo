## Why

小程序欢迎页已就绪，但用户进入后尚未建立身份。需要以**手机号**作为跨端（小程序 + 未来 Web）统一标识，通过微信手机号授权完成注册/登录，并绑定微信 openid 以支持后续微信能力。

## What Changes

- Flyway V2：扩展 `user_account`（country_code、last_login_at），新建 `user_wechat_bind`（方案 B）
- 后端：`POST /api/auth/mini/phone-login`（强制 wx.login code + getPhoneNumber code）
- 后端：WeChatClient（code2Session + getPhoneNumber）、JwtService、AuthService
- 小程序：欢迎页 CTA 前强制手机号授权登录
- 小程序：`services/auth.ts` 封装 wx.login + 手机号授权 + API 调用
- 配置：小程序 AppID `wxbda744f66076ee8e`（via 环境变量）

## Non-goals

- Web 端短信登录（未来 change，共用 phone 主表）
- 微信开放平台 unionid 多端打通（表结构预留 union_id，本 change 不强制）
- 昵称头像授权（getUserProfile）
- 登录态刷新/续期策略优化
- 回访用户跳过欢迎页逻辑

## Capabilities

### New Capabilities

- `backend-phone-auth`: 用户表扩展、微信绑定表、手机号登录 API、JWT 签发
- `mini-welcome-auth`: 欢迎页手机号授权登录 UI 与 auth service 集成

### Modified Capabilities

- `mini-welcome-screen`: 欢迎页 CTA 改为需登录后跳转创建契约

## Impact

- **Backend**: `services/credo-server` — migration V2、entity/mapper/service/controller、测试
- **Mini**: `apps/mini/credo` — welcome page、auth service、project.config appid
- **Config**: `WECHAT_MINI_APP_ID`、`WECHAT_MINI_APP_SECRET`、`JWT_SECRET` 环境变量
- **Depends on**: V1 `user_account` 表、welcome-screen 页面
