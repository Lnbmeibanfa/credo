## ADDED Requirements

### Requirement: User account schema extension

The system SHALL apply Flyway migration `V2__extend_user_account_and_wechat_bind.sql` that:

- Adds `country_code VARCHAR(8) NOT NULL DEFAULT '86'` to `user_account`
- Adds `last_login_at DATETIME NULL` to `user_account`
- Creates `user_wechat_bind` table with foreign key to `user_account(id)`

The migration MUST NOT modify `V1__init_user_account.sql`.

#### Scenario: Migration applies successfully

- **WHEN** Flyway runs on a database with V1 applied
- **THEN** `user_account` has new columns and `user_wechat_bind` table exists

### Requirement: WeChat bind uniqueness

The system SHALL enforce `open_id` uniqueness in `user_wechat_bind` and associate each open_id with exactly one `user_id` for the configured mini program AppID.

#### Scenario: Openid bound to user

- **WHEN** a user logs in with openid "oABC123"
- **THEN** `user_wechat_bind` contains one row linking that open_id to the user's id

### Requirement: Mini program phone login API

The system SHALL expose `POST /api/auth/mini/phone-login` accepting JSON body:

```json
{
  "loginCode": "string, required",
  "phoneCode": "string, required"
}
```

Both codes MUST be required. Missing either returns 400 with stable error code.

#### Scenario: Missing loginCode

- **WHEN** client POSTs with phoneCode only
- **THEN** response is 400 with error code indicating invalid parameters

#### Scenario: Missing phoneCode

- **WHEN** client POSTs with loginCode only
- **THEN** response is 400 with error code indicating invalid parameters

### Requirement: Phone login registers new user

When phone number from WeChat does not exist in `user_account`, the system SHALL create a new user row and bind WeChat openid, then return JWT.

#### Scenario: New user registration

- **WHEN** phone "13800138000" is not in database and both codes are valid
- **THEN** a new `user_account` row is created with that phone and country_code
- **THEN** `user_wechat_bind` links openid to the new user
- **THEN** response includes `isNewUser: true` and valid JWT token

### Requirement: Phone login for existing user

When phone number exists and user status is active (1), the system SHALL update last_login_at, upsert wechat bind, and return JWT.

#### Scenario: Existing active user login

- **WHEN** phone exists with status 1 and codes are valid
- **THEN** no duplicate user_account row is created
- **THEN** response includes `isNewUser: false` and valid JWT token

#### Scenario: Disabled user rejected

- **WHEN** phone exists with status 0
- **THEN** response is 403 with stable error code indicating account disabled

### Requirement: WeChat API integration

The system SHALL call WeChat APIs:

- `code2Session` with loginCode to obtain openid (and unionid if present)
- `getPhoneNumber` with phoneCode to obtain purePhoneNumber and countryCode

WeChat credentials MUST come from environment variables `WECHAT_MINI_APP_ID` and `WECHAT_MINI_APP_SECRET`. AppID for this project is `wxbda744f66076ee8e`.

#### Scenario: Invalid WeChat code

- **WHEN** WeChat returns error for expired or invalid code
- **THEN** API returns 400 with stable error code, no user created

### Requirement: Unified API response

All auth API responses MUST use unified format `{ success, code, message, data }`.

Successful login data MUST include: `token`, `user` (id, phone, nickname, avatarUrl), `isNewUser`.

#### Scenario: Success response shape

- **WHEN** login succeeds
- **THEN** response matches unified success format with token in data

### Requirement: JWT issuance

The system SHALL issue JWT on successful login containing user id as subject, valid for configured expire-hours.

#### Scenario: Token contains user identity

- **WHEN** login succeeds for user id 42
- **THEN** returned JWT can be validated and resolves to user id 42
