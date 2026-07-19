## 1. Ledger timeline query helper (TDD)

- [x] 1.1 Add unit tests for `buildLedgerTimelineQuery` in `utils/ledgerView.test.ts` (returns `{ to: today }`, no `status`)
- [x] 1.2 Implement `buildLedgerTimelineQuery` in `utils/ledgerView.ts`

## 2. Sleep ledger page timeline view

- [x] 2.1 Update `pages/ledger/index.tsx` to load daily-view via `buildLedgerTimelineQuery()` instead of `buildPendingQueueQuery()`
- [x] 2.2 Update ledger page subtitle and empty-state copy for timeline semantics (not pending-queue)
- [x] 2.3 Verify PENDING days remain actionable and FULFILLED/BREACH render read-only after refresh (manual or extend tests if practical)

## 3. Post-sign redirect

- [x] 3.1 After successful `upsertSleepContract` in `pages/contract-create/index.tsx`, call `Taro.redirectTo` to `/pages/ledger/index` (initial sign and re-sign)
- [x] 3.2 Remove or demote redundant manual「去睡眠账本登记」secondary button if redirect makes it unnecessary (keep only if still needed for already-signed revisit without re-submit)

## 4. Verification

- [x] 4.1 Run `pnpm test` and `tsc` in `apps/mini/credo`
- [x] 4.2 Manual smoke: sign contract → lands on ledger; register a day → day stays visible as FULFILLED/BREACH; future dates not listed
