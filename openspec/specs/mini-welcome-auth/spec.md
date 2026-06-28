### Requirement: Welcome page phone authorization

The welcome screen SHALL require phone number authorization before allowing navigation to contract-create.

When user has no stored auth token, the page MUST display a phone authorization button using WeChat `openType="getPhoneNumber"`.

#### Scenario: Unauthenticated user sees auth button

- **WHEN** user opens welcome page without stored token
- **THEN** phone authorization button is visible instead of or before the start contract CTA

#### Scenario: Authenticated user sees CTA

- **WHEN** user opens welcome page with valid stored token
- **THEN**「开始立约」CTA is available

### Requirement: Mandatory wx.login before phone login

The mini program MUST call `Taro.login()` to obtain loginCode and pass it together with phoneCode to the backend on every phone authorization.

#### Scenario: Dual code submission

- **WHEN** user completes phone authorization
- **THEN** auth service sends both loginCode and phoneCode to `/api/auth/mini/phone-login`

### Requirement: Auth service layer

Phone login API calls MUST live in `services/auth.ts`, not in page components.

The service MUST:

- Call wx.login for loginCode
- Accept phoneCode from getPhoneNumber callback
- POST to backend phone-login endpoint
- Store returned token locally (e.g. Taro.setStorageSync)

#### Scenario: Page uses auth service

- **WHEN** welcome page handles phone authorization
- **THEN** it delegates to `services/auth.ts` without direct Taro.request in page

### Requirement: Token storage and CTA gate

After successful login, the mini program SHALL store JWT and enable the「开始立约」button to navigate to contract-create.

#### Scenario: CTA after login

- **WHEN** phone login API returns success
- **THEN** token is stored and user can tap CTA to navigate to `/pages/contract-create/index`

#### Scenario: CTA blocked before login

- **WHEN** user has no token
- **THEN** tapping start contract without completing auth does not navigate to contract-create

### Requirement: Mini program AppID configuration

The mini program project MUST use AppID `wxbda744f66076ee8e` in project configuration for WeChat DevTools and production builds.

#### Scenario: Project config appid

- **WHEN** developer imports project in WeChat DevTools
- **THEN** project.config.json appid is `wxbda744f66076ee8e`
