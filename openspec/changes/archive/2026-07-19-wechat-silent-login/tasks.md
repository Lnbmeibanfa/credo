## 1. Database (phone nullable)

- [x] 1.1 Add Flyway migration test or verify migration naming/next version (V5) expectation in project docs/tests as applicable
- [x] 1.2 Add Flyway migration `V5__user_account_phone_nullable.sql` making `user_account.phone` NULL while keeping `uk_phone`

## 2. Backend WeChat login (TDD)

- [x] 2.1 Add/adjust AuthController and AuthService tests for `POST /api/auth/mini/wechat-login` (new user, returning user, disabled, missing loginCode, WeChat failure)
- [x] 2.2 Implement `wechat-login` endpoint and service flow (code2Session → bind lookup → create or update → JWT)
- [x] 2.3 Remove `phone-login` endpoint and update/remove obsolete phone-login tests
- [x] 2.4 Allow null phone in JWT claim / UserDto serialization; keep parseUserId-only auth

## 3. Mini program auth (TDD)

- [x] 3.1 Update `services/auth.ts` tests for wechat login payload (loginCode only) and remove phoneCode login helpers usage
- [x] 3.2 Implement `wechatLogin()` (or rename) calling `/api/auth/mini/wechat-login`; remove `phoneLogin` / getPhoneNumber path
- [x] 3.3 Update welcome page: button-triggered WeChat login CTA; remove `openType="getPhoneNumber"`; gate「开始立约」on token

## 4. Verification

- [x] 4.1 Run `mvn test` in `services/credo-server`
- [x] 4.2 Run `pnpm test` and `tsc` in `apps/mini/credo`
- [x] 4.3 Deploy migration + backend; upload mini experience build; smoke on real device (tap login → CTA → contract/ledger)
