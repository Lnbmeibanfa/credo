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

The system SHALL provide a sleep ledger page focused on the **pending registration queue** for the active sleep contract.

The page MUST:

- Display summary stats (obligation, pending, fulfilled, breach) — unchanged from full contract stats
- Request daily-view via `services/ledger.ts` with backend filter params: `status=PENDING` and `to=<today ISO date>`
- Render returned days using `LedgerTimeline` and `LedgerItem`
- Highlight PENDING days as actionable (all listed days SHOULD be PENDING when filter applied)
- NOT render UPCOMING, FULFILLED, or BREACH days in the default list view

When the filtered daily-view returns zero days and user has an active contract, the page MUST show an empty state indicating no pending registrations (distinct from no-contract empty state).

Pages MUST NOT filter day status client-side for the default queue view; filtering MUST be delegated to the API via service params.

#### Scenario: Pending day registration

- **WHEN** user taps a PENDING day and confirms FULFILLED
- **THEN** exactly one POST record request is sent and list refreshes with updated pending queue

#### Scenario: Future days not shown

- **WHEN** contract end_date is after today
- **THEN** ledger list does not display UPCOMING days

#### Scenario: Recorded days not shown

- **WHEN** user has FULFILLED or BREACH days in contract period
- **THEN** those days do not appear in the default ledger list

#### Scenario: Backfill past pending day

- **WHEN** user registers a PENDING day from two days ago
- **THEN** single POST succeeds and that day disappears from pending queue after refresh

#### Scenario: All days registered empty state

- **WHEN** filtered daily-view returns empty `days` and user has active contract
- **THEN** page shows empty state message that no pending registrations remain

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

### Requirement: Ledger pending queue query helper

`utils/ledgerView.ts` MUST expose a helper to build the default pending-queue query params (`status: 'PENDING'`, `to: today`) for use by `services/ledger.ts` and the ledger page.

#### Scenario: Helper produces today-bound pending query

- **WHEN** `buildPendingQueueQuery('2026-07-04')` is called
- **THEN** it returns `{ status: 'PENDING', to: '2026-07-04' }`

### Requirement: Ledger service passes query params

`getSleepDailyView(params?)` in `services/ledger.ts` MUST accept optional `status` string and forward it to the daily-view API as query parameters alongside existing `from` and `to`.

#### Scenario: Service forwards status filter

- **WHEN** ledger page calls `getSleepDailyView({ status: 'PENDING', to: '2026-07-04' })`
- **THEN** GET request includes both query parameters
