## 1. Database Migration

- [x] 1.1 Create Flyway migration `V2__extend_user_account_and_wechat_bind.sql` per design.md
- [x] 1.2 Verify migration applies on clean DB after V1 (manual or integration test)

## 2. Backend — Tests First

- [x] 2.1 Write AuthService unit tests (new user, existing user, disabled user, invalid codes, openid bind)
- [x] 2.2 Write AuthController `@WebMvcTest` tests (success, missing params, error responses)

## 3. Backend — Implementation

- [x] 3.1 Add entities `UserAccount`, `UserWechatBind` and MyBatis-Plus mappers
- [x] 3.2 Implement `WeChatClient` (code2Session + getPhoneNumber)
- [x] 3.3 Implement `JwtService` and `WeChatProperties` config
- [x] 3.4 Implement `AuthService` phone login orchestration
- [x] 3.5 Implement `AuthController` POST `/api/auth/mini/phone-login`
- [x] 3.6 Add unified `ApiResponse`, `ErrorCode`, global exception handler

## 4. Mini Program — Auth Service

- [x] 4.1 Write unit tests for `services/auth.ts` (token storage helpers, request payload shape)
- [x] 4.2 Implement `services/auth.ts` (login + phoneLogin + token get/set)
- [x] 4.3 Update `project.config.json` appid to `wxbda744f66076ee8e`

## 5. Mini Program — Welcome Page

- [x] 5.1 Update welcome page: show phone auth button when no token
- [x] 5.2 Gate CTA on authenticated state; wire phone auth → login → enable CTA
- [x] 5.3 Add auth-related copy to `welcomeContent.ts` if needed

## 6. Verification

- [x] 6.1 Run `mvn test` in credo-server
- [x] 6.2 Run `pnpm test`, `tsc --noEmit`, `eslint` in apps/mini/credo
- [x] 6.3 Manual test in WeChat DevTools: auth flow → CTA → contract-create
