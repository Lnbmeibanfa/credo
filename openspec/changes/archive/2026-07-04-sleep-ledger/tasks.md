## 1. Database Migration

- [x] 1.1 Create Flyway migration `V4__init_sleep_ledger.sql` per design.md
- [x] 1.2 Extend FlywayMigrationTest to verify V4 table and unique constraint

## 2. Backend — Tests First

- [x] 2.1 Write `SleepLedgerService` unit tests (record, duplicate, date validation, daily-view status, summary)
- [x] 2.2 Write `LedgerController` `@WebMvcTest` tests (POST record, GET daily-view, GET summary, 400/409 cases)

## 3. Backend — Implementation

- [x] 3.1 Add `SleepLedgerEvent` entity and mapper
- [x] 3.2 Implement `SleepLedgerService` record, daily-view, and summary logic
- [x] 3.3 Implement `LedgerController` POST `/api/ledger/sleep/records`, GET daily-view, GET summary
- [x] 3.4 Add ledger DTOs, error codes (e.g. DUPLICATE_RECORD, NO_CONTRACT), reuse JWT auth

## 4. Mini — Utils & Service Tests

- [x] 4.1 Write unit tests for `utils/ledgerView.ts` (status mapping, weekday labels)
- [x] 4.2 Write unit tests for `services/ledger.ts` (request payload shape, auth header)

## 5. Mini — UI Components

- [x] 5.1 Extend `creditEventTypes.ts` with PENDING and UPCOMING visuals
- [x] 5.2 Update `StatusIcon` and `StatusTag` for PENDING / UPCOMING
- [x] 5.3 Ensure `LedgerItem` supports pending/upcoming rows (tap affordance for PENDING only)

## 6. Mini — Ledger Service & Page

- [x] 6.1 Implement `services/ledger.ts` (recordSleepDay, getSleepDailyView, getSleepLedgerSummary)
- [x] 6.2 Implement `utils/ledgerView.ts` pure helpers
- [x] 6.3 Create sleep ledger page with summary, timeline, single-day confirm registration
- [x] 6.4 Register page route and add navigation entry from welcome or post-contract flow
- [x] 6.5 Handle no-contract empty state with link to contract-create

## 7. Verification

- [x] 7.1 Run `mvn test` in credo-server
- [x] 7.2 Run `pnpm test`, `tsc --noEmit`, `eslint` in apps/mini/credo
- [ ] 7.3 Manual test in WeChat DevTools: pending → register → backfill past day → summary counts
