## MODIFIED Requirements

### Requirement: Sleep ledger page

The system SHALL provide a sleep ledger page focused on the **timeline of obligation days up to today** for the active sleep contract.

The page MUST:

- Display summary stats (obligation, pending, fulfilled, breach) — unchanged from full contract stats
- Request daily-view via `services/ledger.ts` with backend params: `to=<today ISO date>` and **no** `status` filter
- Render returned days using `LedgerTimeline` and `LedgerItem`, sorted with most recent date first
- Render PENDING, FULFILLED, and BREACH days within the returned range
- Treat PENDING days as actionable; FULFILLED and BREACH days as read-only
- NOT render days after today (UPCOMING); achieved by `to=today` on the API request

When the daily-view returns zero days and user has an active contract, the page MUST show an empty state indicating no obligation days in range yet (distinct from no-contract empty state).

Pages MUST NOT filter day status client-side for the default timeline view; date upper bound MUST be delegated to the API via service params.

#### Scenario: Pending day registration

- **WHEN** user taps a PENDING day and confirms FULFILLED
- **THEN** exactly one POST record request is sent and list refreshes showing that day as FULFILLED (still visible)

#### Scenario: Future days not shown

- **WHEN** contract end_date is after today
- **THEN** ledger list does not display days after today

#### Scenario: Recorded days remain visible

- **WHEN** user has FULFILLED or BREACH days on or before today
- **THEN** those days appear in the default ledger list with read-only styling

#### Scenario: Backfill past pending day

- **WHEN** user registers a PENDING day from two days ago
- **THEN** single POST succeeds and that day remains in the list with FULFILLED or BREACH status after refresh

#### Scenario: No obligation days empty state

- **WHEN** daily-view returns empty `days`, user has active contract, and no obligation days exist up to today (e.g. contract starts in the future)
- **THEN** page shows empty state message that no obligation days are available yet

## REMOVED Requirements

### Requirement: Ledger pending queue query helper

**Reason**: Default ledger view changed from pending-only queue to full timeline up to today; replaced by `buildLedgerTimelineQuery`.

**Migration**: Ledger page and default load path MUST use `buildLedgerTimelineQuery` instead of `buildPendingQueueQuery`.

## ADDED Requirements

### Requirement: Ledger timeline query helper

`utils/ledgerView.ts` MUST expose a helper to build the default timeline query params (`to: today`) without a `status` filter, for use by `services/ledger.ts` and the ledger page.

#### Scenario: Helper produces today-bound timeline query

- **WHEN** `buildLedgerTimelineQuery('2026-07-11')` is called
- **THEN** it returns `{ to: '2026-07-11' }` with no `status` property
