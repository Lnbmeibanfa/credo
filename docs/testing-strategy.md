# Testing Strategy

Credo follows **Pragmatic TDD**: test-first for core business logic; integration tests for CRUD and DB; frontend focuses on `services/` and `utils/`.

## Principles

1. Tests express OpenSpec behavior — if a spec says it, a test should prove it.
2. Test tasks appear before implementation tasks in every OpenSpec change.
3. Prefer fast unit tests; use integration tests where boundaries matter (DB, HTTP).
4. No UI snapshot or E2E tests in MVP.

## Backend Test Pyramid

```
        ╱╲
       ╱  ╲         Integration (few, critical paths)
      ╱────╲        @SpringBootTest + Testcontainers MySQL
     ╱      ╲
    ╱ Service╲      Unit tests (primary)
   ╱  Tests   ╲     Mockito, no Spring context
  ╱────────────╲
 ╱  Controller  ╲   Contract tests
╱    Tests      ╲   @WebMvcTest + mocked services
```

### Layer Guide

| Layer | Tool | Scope |
|-------|------|-------|
| Service | JUnit 5 + Mockito | Business rules, state transitions, validation |
| Controller | `@WebMvcTest` + MockMvc | HTTP status, response format, input validation |
| Mapper / DB | Testcontainers + MySQL | SQL correctness, Flyway migration compatibility |
| Full flow | `@SpringBootTest` + Testcontainers | End-to-end through HTTP → service → DB |

### Persistence: MyBatis-Plus

Use MyBatis-Plus mapper interfaces (not JPA). Do not use `@DataJpaTest`.

For mapper-level tests, spin up Testcontainers MySQL, run Flyway migrations, then assert query results.

### Required Scenarios (Core Flows)

Every backend core flow must cover:

1. **Successful path** — happy case returns expected data and status.
2. **Invalid parameters** — bad input returns 400 with stable error code.
3. **Duplicate submission** — repeated action handled correctly (409 or idempotent).
4. **Illegal state transition** — action rejected when state does not allow it.
5. **Permission / ownership boundary** — unauthorized access returns 401/403.

### External Dependencies

Mock external APIs (WeChat, etc.) in unit and controller tests.
Use WireMock or a hand-written fake implementing the client interface.

### Flyway in Tests

Test environment must run the same Flyway migrations as production.
Never edit an applied migration — add a new `V{n}__*.sql` instead.

Verify migrations pass in CI before merge.

## Frontend Test Strategy (Mini Program)

### Test These

| Target | What to test |
|--------|-------------|
| `services/` | Request building, response parsing, error code mapping |
| `utils/` | Pure functions — formatters, validators, transformers |
| `stores/` | State transformation logic (if non-trivial) |

### Skip in MVP

- Component snapshot tests
- WeChat DevTools E2E automation

### Tooling (to be added)

Vitest or Jest for unit tests. Run via `npm test` in `apps/mini/credo/`.

## Test Naming

Use descriptive names with `@DisplayName` (backend) or clear `describe/it` blocks (frontend):

```
// Backend
@Test
@DisplayName("should reject login when phone is blank")
void login_blankPhone_returns400() { ... }

// Frontend
describe('formatSleepDuration', () => {
  it('returns hours and minutes for valid input', () => { ... })
})
```

## CI Gate

PR merge requires all of the following to pass:

- `mvn test` in `services/credo-server/`
- `tsc --noEmit` in `apps/mini/credo/`
- `eslint` in `apps/mini/credo/`
- `npm test` in `apps/mini/credo/` (once test framework is installed)

## OpenSpec Integration

When creating tasks for a change:

1. Write spec scenarios first (Given/When/Then or bullet acceptance criteria).
2. Add test tasks that map 1:1 to spec scenarios.
3. Add implementation tasks after test tasks.
4. DB changes include a Flyway migration task before service/controller tasks.
