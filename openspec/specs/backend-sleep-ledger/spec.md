### Requirement: Sleep ledger database schema

The system SHALL apply Flyway migration `V4__init_sleep_ledger.sql` creating `sleep_ledger_event` with:

- `user_id`, `contract_id`, `record_date`, `event_type`, `note`, `created_at`
- `UNIQUE (contract_id, record_date)` enforcing one record per sleep day per contract

The table MUST NOT store PENDING or UPCOMING rows; those are computed at read time.

#### Scenario: Migration applies successfully

- **WHEN** Flyway runs after V3
- **THEN** `sleep_ledger_event` exists with foreign keys to `user_account` and `contract`

### Requirement: Append-only ledger events

The system SHALL only INSERT into `sleep_ledger_event`. UPDATE and DELETE of ledger rows MUST NOT be exposed via API.

Allowed `event_type` values for MVP MUST be `FULFILLED` and `BREACH` only.

#### Scenario: Record is immutable

- **WHEN** a ledger event exists for a contract and record_date
- **THEN** no API allows changing its event_type or deleting the row

### Requirement: Single-day record API

The system SHALL expose `POST /api/ledger/sleep/records` (JWT required) accepting one record per request:

```json
{ "recordDate": "YYYY-MM-DD", "eventType": "FULFILLED|BREACH", "note": "optional" }
```

The API MUST reject batch payloads; each call creates at most one ledger row.

#### Scenario: Successful fulfillment record

- **WHEN** user with active sleep contract POSTs valid FULFILLED for an eligible record_date
- **THEN** one row is inserted and response includes event details

#### Scenario: Duplicate record rejected

- **WHEN** user POSTs for a record_date that already has a ledger row
- **THEN** response is 409 with stable error code

#### Scenario: Future date rejected

- **WHEN** recordDate is after today
- **THEN** response is 400 with stable error code

#### Scenario: Out of contract range rejected

- **WHEN** recordDate is before start_date or after end_date
- **THEN** response is 400 with stable error code

#### Scenario: No active contract

- **WHEN** user has no sleep contract and POSTs a record
- **THEN** response is 404 with stable error code

### Requirement: Daily view API

The system SHALL expose `GET /api/ledger/sleep/daily-view` returning each day within the user's active sleep contract range with computed status:

- `UPCOMING` when record_date > today
- `PENDING` when record_date ≤ today and no ledger row exists
- `FULFILLED` or `BREACH` when a ledger row exists

Response MUST include `recordDate`, `status`, and when recorded: `eventType`, `createdAt`.

#### Scenario: Pending day in range

- **WHEN** contract includes 2026-06-26, today is 2026-06-28, and no row exists for 2026-06-26
- **THEN** daily-view includes that day with status PENDING

#### Scenario: Upcoming day

- **WHEN** contract end_date is after today
- **THEN** days after today within contract range have status UPCOMING

### Requirement: Ledger summary API

The system SHALL expose `GET /api/ledger/sleep/summary` returning counts for the active sleep contract:

- `obligationDays`: inclusive days from start_date to min(end_date, today)
- `recordedDays`: count of ledger rows with record_date ≤ today
- `pendingDays`: obligationDays - recordedDays
- `fulfilledDays`: count where event_type = FULFILLED
- `breachDays`: count where event_type = BREACH

#### Scenario: Summary with mixed states

- **WHEN** obligationDays is 7, user recorded 3 FULFILLED and 1 BREACH
- **THEN** pendingDays is 3 and recordedDays is 4

### Requirement: Unified API response

Ledger APIs MUST use `{ success, code, message, data }` format.

#### Scenario: Record success shape

- **WHEN** POST record succeeds
- **THEN** response data includes id, contractId, recordDate, eventType, createdAt
