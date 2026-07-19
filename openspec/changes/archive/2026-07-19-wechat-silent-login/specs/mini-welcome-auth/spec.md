## MODIFIED Requirements

### Requirement: Welcome page phone authorization

The welcome screen SHALL require authentication before allowing navigation to contract-create.

When user has no stored auth token, the page MUST display a WeChat login button (not `openType="getPhoneNumber"`). Tapping the button MUST call `Taro.login()` / `wx.login` and then the auth service wechat-login API.

The page MUST NOT auto-login on show solely for first-time auth without user tap (button-triggered login is required).

#### Scenario: Unauthenticated user sees login button

- **WHEN** user opens welcome page without stored token
- **THEN** a WeChat login button is visible instead of the start-contract CTA

#### Scenario: Authenticated user sees CTA

- **WHEN** user opens welcome page with valid stored token
- **THEN**「开始立约」CTA is available

### Requirement: Mandatory wx.login before phone login

The mini program MUST call `Taro.login()` to obtain loginCode and pass it to the backend wechat-login API on every login button tap.

#### Scenario: LoginCode submission

- **WHEN** user taps WeChat login
- **THEN** auth service sends loginCode to `/api/auth/mini/wechat-login`

### Requirement: Auth service layer

WeChat login API calls MUST live in `services/auth.ts`, not in page components.

The service MUST:

- Call wx.login / Taro.login for loginCode
- POST to backend wechat-login endpoint with loginCode only
- Store returned token locally (e.g. Taro.setStorageSync)

Pages MUST NOT call `Taro.request` directly for auth endpoints.

#### Scenario: Page uses auth service

- **WHEN** welcome page handles WeChat login
- **THEN** it delegates to `services/auth.ts` without direct Taro.request in page

### Requirement: Token storage and CTA gate

After successful login, the mini program SHALL store JWT and enable the「开始立约」button to navigate to contract-create.

#### Scenario: CTA after login

- **WHEN** wechat-login API returns success
- **THEN** token is stored and user can tap CTA to navigate to `/pages/contract-create/index`

#### Scenario: CTA blocked before login

- **WHEN** user has no token
- **THEN** tapping start contract without completing auth does not navigate to contract-create
