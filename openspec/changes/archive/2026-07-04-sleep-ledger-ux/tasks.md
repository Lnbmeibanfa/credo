## 1. Backend — daily-view status filter

- [x] 1.1 Add unit tests for `SleepLedgerService.getDailyView` with `status` param: PENDING-only, multi-status, invalid status 400, no param backward compatible
- [x] 1.2 Implement `status` parsing and post-filter in `SleepLedgerService`; extend `LedgerController` with `@RequestParam status`
- [x] 1.3 Add controller tests for `GET /daily-view?status=PENDING&to=...` and invalid status
- [x] 1.4 Verify filtered response preserves unfiltered `summary` in service tests

## 2. Mini — ledger query params & utils

- [x] 2.1 Add unit tests for `buildPendingQueueQuery` in `utils/ledgerView.test.ts`
- [x] 2.2 Implement `buildPendingQueueQuery(today?)` in `utils/ledgerView.ts`
- [x] 2.3 Extend `getSleepDailyView` in `services/ledger.ts` to accept and forward `status` query param

## 3. Mini — PageHeader back action

- [x] 3.1 Add styles for context-mode back control in `PageHeader/index.scss`
- [x] 3.2 Extend `PageHeader` context mode with optional `onBack` prop and back UI
- [x] 3.3 Wire `onBack={() => Taro.navigateBack()}` on `contract-create/index.tsx`

## 4. Mini — ledger page pending queue

- [x] 4.1 Update ledger page to call `getSleepDailyView` with pending-queue params (`status=PENDING`, `to=today`)
- [x] 4.2 Add empty state UI when filtered `days` is empty and contract exists (distinct from no-contract state)
- [x] 4.3 Adjust ledger page subtitle/copy to clarify list shows pending registrations only; keep summary cards

## 5. Verification

- [x] 5.1 Run `mvn test` in `services/credo-server`
- [x] 5.2 Run `pnpm test` and `pnpm exec tsc --noEmit` in `apps/mini/credo`
