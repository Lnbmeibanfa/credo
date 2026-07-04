## 1. Database Migration

- [x] 1.1 Create Flyway migration `V3__init_sleep_contract.sql` per design.md
- [x] 1.2 Verify migration applies after V2 (integration test or Testcontainers)

## 2. Backend — Tests First

- [x] 2.1 Write `SleepContractService` unit tests (create, update, validation, one-per-user, clause replace)
- [x] 2.2 Write `ContractController` `@WebMvcTest` tests (GET mine, PUT upsert, 400 cases)

## 3. Backend — Implementation

- [x] 3.1 Add entities `Contract`, `SleepContract`, `ContractBreachClause` and mappers
- [x] 3.2 Implement `SleepContractService` upsert and getMine orchestration
- [x] 3.3 Implement `ContractController` GET `/api/contracts/sleep/mine` and PUT `/api/contracts/sleep`
- [x] 3.4 Add contract DTOs, error codes, and JWT user extraction for contract APIs

## 4. Mini — Utils & Service Tests

- [x] 4.1 Write unit tests for `utils/contractForm.ts` (duration days, clause validation, DTO mapping)
- [x] 4.2 Write unit tests for `services/contract.ts` (request payload shape, auth header)

## 5. Mini — Components

- [x] 5.1 Implement `DateRangeField` form component
- [x] 5.2 Implement `BreachClauseSelector` form component
- [x] 5.3 Implement `SleepContractDocument` data-display component
- [x] 5.4 Export new components from barrel files

## 6. Mini — Contract Service & Page

- [x] 6.1 Implement `services/contract.ts` (getMySleepContract, upsertSleepContract)
- [x] 6.2 Implement `utils/contractForm.ts` pure helpers
- [x] 6.3 Replace contract-create placeholder with full SCREEN 01 form + document binding
- [x] 6.4 Wire confirm sign button to upsert API; load existing contract on page entry

## 7. Verification

- [x] 7.1 Run `mvn test` in credo-server
- [x] 7.2 Run `pnpm test`, `tsc --noEmit`, `eslint` in apps/mini/credo
- [ ] 7.3 Manual test in WeChat DevTools: form → live document preview → sign → reload edit
