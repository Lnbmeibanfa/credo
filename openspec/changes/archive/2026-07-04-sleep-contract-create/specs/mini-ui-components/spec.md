## ADDED Requirements

### Requirement: Date range field component

The system SHALL provide `DateRangeField` in `components/form/` for selecting inclusive start and end dates at day granularity.

The component MUST display selected range and computed total days (共 N 天).

#### Scenario: Day granularity selection

- **WHEN** user selects start and end dates
- **THEN** values are emitted as YYYY-MM-DD strings

#### Scenario: Total days shown

- **WHEN** range spans 30 inclusive days
- **THEN** component displays 共 30 天

### Requirement: Breach clause selector component

The system SHALL provide `BreachClauseSelector` in `components/form/` for sleep contract breach clause configuration.

RECORD row MUST be mandatory and locked. REVIEW and CUSTOM MUST be toggleable. CUSTOM MUST show editable text when enabled.

#### Scenario: Mandatory record row

- **WHEN** BreachClauseSelector renders
- **THEN** RECORD cannot be unchecked

### Requirement: Sleep contract document component

The system SHALL provide `SleepContractDocument` in `components/data-display/` (or `components/brand/`) rendering formal contract document with preview and signed modes.

#### Scenario: Component export

- **WHEN** contract-create page imports document component
- **THEN** it is available from components barrel export
