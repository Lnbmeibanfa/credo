## ADDED Requirements

### Requirement: Nullable phone on user account

The system SHALL apply a new Flyway migration (after V4) that changes `user_account.phone` from `NOT NULL` to nullable `VARCHAR(20) NULL`, while retaining the unique key `uk_phone`.

Existing rows with non-null phone MUST remain valid. Multiple users with `phone IS NULL` MUST be allowed.

#### Scenario: Migration allows null phone

- **WHEN** Flyway migrates a database that already has V4 applied
- **THEN** inserting a `user_account` row with `phone = NULL` succeeds

### Requirement: Mini program WeChat login API

The system SHALL expose `POST /api/auth/mini/wechat-login` accepting JSON body:

```json
{
  "loginCode": "string, required"
}
```

Missing or blank `loginCode` MUST return 400 with stable error code `INVALID_PARAMETER`.

#### Scenario: Missing loginCode

- **WHEN** client POSTs without loginCode
- **THEN** response is 400 with code `INVALID_PARAMETER`

### Requirement: WeChat login registers new user by openid

When `code2Session` returns an openid that is not bound in `user_wechat_bind`, the system SHALL create a new `user_account` with `phone` null (and default `country_code`), insert `user_wechat_bind`, and return JWT with `isNewUser: true`.

#### Scenario: First login creates user

- **WHEN** openid has no bind and loginCode is valid
- **THEN** a new user_account exists with null phone
- **THEN** user_wechat_bind links that openid to the new user
- **THEN** response includes `isNewUser: true` and a valid JWT

### Requirement: WeChat login for existing bound user

When openid is already bound to an active user (status 1), the system SHALL update `last_login_at`, MUST NOT create a duplicate user_account, and return JWT with `isNewUser: false`.

#### Scenario: Returning user

- **WHEN** openid is bound to status-1 user
- **THEN** no new user_account row is created
- **THEN** response includes `isNewUser: false` and JWT for that user id

#### Scenario: Disabled user rejected

- **WHEN** openid is bound to status-0 user
- **THEN** response is 403 with code `ACCOUNT_DISABLED`

### Requirement: WeChat login uses code2Session only

WeChat login MUST call `code2Session` with loginCode to obtain openid (and unionid if present). It MUST NOT require or call WeChat `getPhoneNumber` for login.

WeChat credentials MUST come from `WECHAT_MINI_APP_ID` and `WECHAT_MINI_APP_SECRET`.

#### Scenario: Invalid loginCode

- **WHEN** WeChat returns an error for expired or invalid loginCode
- **THEN** API returns 400 with stable WeChat auth error code and creates no user

### Requirement: Login response allows null phone

Successful wechat-login data MUST include: `token`, `user` (id, phone, nickname, avatarUrl), `isNewUser`.

`user.phone` MAY be null. JWT MUST use user id as subject; phone claim MAY be omitted or null.

#### Scenario: Success with null phone

- **WHEN** a new wechat-login user is created
- **THEN** response success payload includes token and user with null phone

## REMOVED Requirements

### Requirement: Mini program phone login API

**Reason**: Personal-subject mini programs cannot use getPhoneNumber on real devices; login identity moves to openid.

**Migration**: Clients MUST call `POST /api/auth/mini/wechat-login` with `loginCode` only.

### Requirement: Phone login registers new user

**Reason**: Replaced by WeChat login registers new user by openid.

**Migration**: Use wechat-login new-user behavior.

### Requirement: Phone login for existing user

**Reason**: Replaced by WeChat login for existing bound user.

**Migration**: Use wechat-login returning-user behavior.

## MODIFIED Requirements

### Requirement: WeChat API integration

The system SHALL call WeChat `code2Session` with loginCode during wechat-login to obtain openid (and unionid if present).

WeChat credentials MUST come from environment variables `WECHAT_MINI_APP_ID` and `WECHAT_MINI_APP_SECRET`.

Login MUST NOT depend on WeChat `getPhoneNumber`.

#### Scenario: Invalid WeChat code

- **WHEN** WeChat returns error for expired or invalid loginCode
- **THEN** API returns 400 with stable error code, no user created

### Requirement: Unified API response

All auth API responses MUST use unified format `{ success, code, message, data }`.

Successful wechat-login data MUST include: `token`, `user` (id, phone, nickname, avatarUrl), `isNewUser`. Field `user.phone` MAY be null.

#### Scenario: Success response shape

- **WHEN** wechat-login succeeds
- **THEN** response matches unified success format with token in data
