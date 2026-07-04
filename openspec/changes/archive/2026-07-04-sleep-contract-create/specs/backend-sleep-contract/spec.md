## ADDED Requirements

### Requirement: Sleep contract database schema

The system SHALL apply Flyway migration `V3__init_sleep_contract.sql` creating:

- `contract` table with `user_id`, `type`, `contract_no`, `status`, `start_date`, `end_date`, `signed_at`
- `sleep_contract` table with `contract_id` FK and `target_bedtime` TIME
- `contract_breach_clause` table with `clause_type`, `enabled`, `content_text`, `sort_order`

The migration MUST enforce `UNIQUE (user_id, type)` on `contract` to allow at most one sleep contract per user.

#### Scenario: Migration applies successfully

- **WHEN** Flyway runs after V2
- **THEN** all three tables exist with foreign keys to `user_account` and `contract`

### Requirement: One sleep contract per user with update

The system SHALL allow each user to have at most one contract with `type = 'SLEEP'`.

When a sleep contract already exists for the user, PUT upsert MUST update the existing row rather than creating a duplicate.

#### Scenario: First contract creation

- **WHEN** user with no sleep contract submits valid upsert payload
- **THEN** a new contract row is created with generated `contract_no`

#### Scenario: Contract update

- **WHEN** user with existing sleep contract submits valid upsert payload
- **THEN** the same contract row is updated and breach clauses are replaced

#### Scenario: Duplicate user type rejected at DB level

- **WHEN** a second INSERT for same user_id and type SLEEP is attempted
- **THEN** database unique constraint prevents duplicate

### Requirement: Sleep contract upsert API

The system SHALL expose:

- `GET /api/contracts/sleep/mine` — return current user's sleep contract or empty when none
- `PUT /api/contracts/sleep` — create or update sleep contract (requires JWT)

Request body MUST include: `targetBedtime` (HH:mm), `startDate`, `endDate` (YYYY-MM-DD), `breachClauses` array.

#### Scenario: Missing targetBedtime

- **WHEN** PUT body omits targetBedtime
- **THEN** response is 400 with stable error code

#### Scenario: Invalid date range

- **WHEN** startDate is after endDate
- **THEN** response is 400 with stable error code

#### Scenario: RECORD clause required

- **WHEN** breachClauses does not include RECORD with enabled true
- **THEN** response is 400 with stable error code

### Requirement: Contract number generation

On first create, the system SHALL generate `contract_no` in format `C-{yyyyMMdd}-{sequence}` unique across all contracts.

On update, `contract_no` MUST NOT change.

#### Scenario: New contract number

- **WHEN** user creates first sleep contract on 2026-06-24
- **THEN** contract_no matches pattern `C-20260624-*`

### Requirement: Breach clause persistence

The system SHALL persist breach clauses as rows in `contract_breach_clause` with types `RECORD`, `REVIEW`, `CUSTOM`.

RECORD MUST always be stored with `enabled = true`.

CUSTOM MAY include `contentText` when enabled.

#### Scenario: All three clause types stored

- **WHEN** upsert includes RECORD enabled, REVIEW enabled, CUSTOM enabled with text
- **THEN** three clause rows exist linked to the contract

### Requirement: Unified API response

Contract APIs MUST use `{ success, code, message, data }` format.

Successful upsert data MUST include contract id, contractNo, targetBedtime, startDate, endDate, breachClauses, signedAt.

#### Scenario: Upsert success shape

- **WHEN** upsert succeeds
- **THEN** response includes token contract fields in data
