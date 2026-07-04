## ADDED Requirements

### Requirement: Daily view status filter parameter

The system SHALL extend `GET /api/ledger/sleep/daily-view` with optional query parameter `status`:

- Format: comma-separated list of status values
- Allowed values: `PENDING`, `UPCOMING`, `FULFILLED`, `BREACH`
- When omitted, response MUST include all days in range (backward compatible)
- When provided, response MUST include only days whose computed status matches one of the requested values

Invalid or unknown status values MUST return 400 with stable error code.

#### Scenario: Filter pending days only

- **WHEN** client requests `daily-view?status=PENDING&to=2026-07-04` and contract has mixed statuses
- **THEN** response `days` array contains only PENDING entries with `recordDate` ≤ 2026-07-04

#### Scenario: Multiple status filter

- **WHEN** client requests `daily-view?status=FULFILLED,BREACH`
- **THEN** response contains only recorded days with matching statuses

#### Scenario: No status parameter returns full range

- **WHEN** client requests `daily-view` without `status`
- **THEN** response includes PENDING, UPCOMING, FULFILLED, and BREACH days as before

#### Scenario: Invalid status rejected

- **WHEN** client requests `daily-view?status=INVALID`
- **THEN** response is 400 with stable error code

### Requirement: Daily view date bounds with filter

The existing `from` and `to` parameters MUST compose with `status`:

- `from` defaults to contract `start_date` when omitted
- `to` defaults to contract `end_date` when omitted
- Date bounds apply before status filtering

#### Scenario: To today excludes future upcoming

- **WHEN** client requests `daily-view?to=2026-07-04` without status
- **THEN** days after 2026-07-04 are not included even if within contract end_date

#### Scenario: Pending queue with to today

- **WHEN** client requests `daily-view?status=PENDING&to=<today>`
- **THEN** no UPCOMING or recorded days appear in `days` array

### Requirement: Filtered daily view preserves summary

When `status` filter is applied, response MUST still include unfiltered `summary` computed for the full active contract (obligation, pending, fulfilled, breach counts unchanged).

#### Scenario: Summary unchanged by list filter

- **WHEN** client requests `daily-view?status=PENDING` and contract has 3 fulfilled days
- **THEN** `summary.fulfilledDays` remains 3 while `days` omits those entries
