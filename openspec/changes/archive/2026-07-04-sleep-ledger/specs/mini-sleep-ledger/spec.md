## ADDED Requirements

### Requirement: Sleep ledger service layer

Ledger API calls MUST live in `services/ledger.ts`.

The service MUST expose at minimum:

- `recordSleepDay(payload)` — POST single-day record
- `getSleepDailyView(params?)` — GET daily-view
- `getSleepLedgerSummary()` — GET summary

Pages MUST NOT call `Taro.request` directly for ledger endpoints.

#### Scenario: Page delegates to service

- **WHEN** user confirms a day record on ledger page
- **THEN** page calls `services/ledger.ts` record method once

### Requirement: Sleep ledger page

The system SHALL provide a sleep ledger page listing days within the active contract period.

The page MUST:

- Display summary stats (obligation, pending, fulfilled, breach)
- Render days using `LedgerTimeline` and `LedgerItem`
- Highlight PENDING days as actionable
- Render UPCOMING days as non-interactive
- Render FULFILLED/BREACH days as read-only

#### Scenario: Pending day registration

- **WHEN** user taps a PENDING day and confirms FULFILLED
- **THEN** exactly one POST record request is sent and list refreshes

#### Scenario: Upcoming day not actionable

- **WHEN** user views a day with status UPCOMING
- **THEN** no registration action is available

#### Scenario: Backfill past pending day

- **WHEN** user registers a PENDING day from two days ago
- **THEN** single POST succeeds and that day shows FULFILLED or BREACH

### Requirement: Single record confirmation UX

Registration MUST be one day at a time. The UI MUST NOT offer multi-select or batch submit in MVP.

Before submit, the UI SHOULD confirm that the record is irreversible.

#### Scenario: No batch submit

- **WHEN** multiple PENDING days exist
- **THEN** user must register each day with separate confirm actions

### Requirement: Auth gate

The ledger page MUST require authenticated user (stored JWT). Unauthenticated access SHOULD redirect or block.

#### Scenario: API includes auth header

- **WHEN** recordSleepDay is called
- **THEN** request includes Authorization header with stored token

### Requirement: No active contract handling

When user has no sleep contract, the ledger page MUST show guidance to create a contract first (navigate to contract-create).

#### Scenario: No contract empty state

- **WHEN** daily-view or summary returns no active contract
- **THEN** page shows empty state with link to contract creation

### Requirement: Credit event read model extension

`constants/creditEventTypes.ts` MUST extend visual mapping for read-model statuses:

- `PENDING` — 待登记
- `UPCOMING` — 未到期

These values MUST NOT be sent as event_type to POST record API.

#### Scenario: Pending visual

- **WHEN** LedgerItem receives statusType PENDING
- **THEN** it displays 待登记 styling distinct from FULFILLED and BREACH
