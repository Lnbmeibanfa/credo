## Why

个人主体微信小程序真机无法使用 `getPhoneNumber`，当前「手机号授权登录」在体验版/真机不可用，阻断朋友邀测与正式体验。需要将登录锚点从微信手机号改为 `openid` 静默换票，使个人主体真机可登录，同时保留后续绑定手机号的扩展空间。

## What Changes

- **BREAKING**：废弃 `POST /api/auth/mini/phone-login` 与小程序 `getPhoneNumber` 登录路径
- 新增 `POST /api/auth/mini/wechat-login`：仅需 `loginCode`（`wx.login`），经 `code2Session` 以 `openid` 找/建用户并发 JWT
- Flyway：`user_account.phone` 改为可空（保留 `uk_phone`；无手机号用户 `phone=NULL`）
- 欢迎页：未登录时展示「微信登录」按钮（点击后 `wx.login` + wechat-login）；**不**进页自动登录
- 已有 `user_wechat_bind` 的用户再次登录按 `openid` 续登，保留契约/账本
- `UserDto.phone` / JWT `phone` claim 允许为空；鉴权仍以 JWT `subject=userId` 为准

## Capabilities

### New Capabilities

（无 — 在现有 auth 能力上替换登录方式）

### Modified Capabilities

- `backend-phone-auth`: 以 openid 微信登录替换手机号双 code 登录；schema 允许 phone 为空；移除对 getPhoneNumber 的登录依赖
- `mini-welcome-auth`: 欢迎页改为按钮触发的微信登录；移除 getPhoneNumber 按钮与 phone-login 调用

## Non-goals

- 绑定/校验手机号（手填或短信）
- 恢复或双轨保留 `phone-login` / `getPhoneNumber` 登录
- 企业主体开通手机号快速验证
- 修改契约、账本 schema 或业务 API
- 清理「仅有 phone、无 wechat bind」的历史脏数据
- 信用分、多契约、社区等未批准能力

## Impact

- **DB**: 新 Flyway 迁移（如 `V5__user_account_phone_nullable.sql`）
- **Backend**: `AuthController` / `AuthService`、DTO、测试；`WeChatClient.getPhoneNumber` 可从登录路径移除（客户端方法可保留但不被登录调用）
- **Mini**: `services/auth.ts`、`pages/index/index.tsx`、相关测试与文案
- **OpenSpec**: delta specs for `backend-phone-auth`, `mini-welcome-auth`
- **Ops**: ECS 部署需跑新迁移；小程序重新上传体验版
