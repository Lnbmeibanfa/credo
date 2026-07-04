## ADDED Requirements

### Requirement: Sleep contract create page

The contract-create page SHALL implement SCREEN 01「创建睡眠契约」with three form sections:

1. **关机入眠时间** — `TimePickerField` bound to `targetBedtime`
2. **合同生效时期** — `DateRangeField` bound to `startDate` and `endDate` (day granularity)
3. **违约条款** — `BreachClauseSelector` bound to breach clause state

The page MUST use `PageShell`, `PageHeader` context mode, and `FormSection` from the design system.

#### Scenario: Page renders form sections

- **WHEN** user navigates to contract-create after login
- **THEN** sections 01, 02, 03 are visible with STEP 01 / 缔约 header

#### Scenario: Existing contract loads for edit

- **WHEN** user has an existing sleep contract from GET mine
- **THEN** form fields are pre-filled with stored values

### Requirement: Date range field component

The system SHALL provide `DateRangeField` component allowing user to select start and end dates at day granularity.

The component MUST display total duration as「共 N 天」computed from inclusive date range.

#### Scenario: Duration display

- **WHEN** startDate is 2026-06-24 and endDate is 2026-07-23
- **THEN** component shows 30 days total

#### Scenario: Date selection updates binding

- **WHEN** user changes end date via picker
- **THEN** parent form state endDate updates

### Requirement: Breach clause selector

The system SHALL provide `BreachClauseSelector` with three items:

1. **记录档案** — mandatory, locked enabled, not deselectable
2. **进行复盘** — optional checkbox
3. **自定义条款** — optional checkbox with editable text area when enabled

#### Scenario: Record clause locked

- **WHEN** BreachClauseSelector renders
- **THEN** RECORD row shows locked/mandatory state and cannot be disabled

#### Scenario: Custom clause editing

- **WHEN** user enables CUSTOM clause
- **THEN** text area is editable and bound to custom content

### Requirement: Form document two-way binding

Form state MUST bind bidirectionally to `SleepContractDocument` so document preview updates in real time as user edits the form.

The page MUST NOT call contract API until user taps「确认签约」.

#### Scenario: Bedtime updates document

- **WHEN** user changes targetBedtime to 23:30
- **THEN** SleepContractDocument Article 1 text reflects 23:30 without API call

#### Scenario: Sign triggers single API call

- **WHEN** user taps confirm sign button with valid form
- **THEN** exactly one PUT /api/contracts/sleep request is sent

### Requirement: Contract service layer

Contract API calls MUST live in `services/contract.ts`.

The service MUST expose `getMySleepContract()` and `upsertSleepContract(payload)`.

Pages MUST NOT call `Taro.request` directly for contract endpoints.

#### Scenario: Page delegates to service

- **WHEN** contract-create page submits
- **THEN** it calls `services/contract.ts` upsert method

### Requirement: JWT required for contract APIs

Contract create page MUST require authenticated user (stored JWT).

Unauthenticated access SHOULD redirect or block with message.

#### Scenario: API includes auth header

- **WHEN** upsertSleepContract is called
- **THEN** request includes Authorization header with stored token
