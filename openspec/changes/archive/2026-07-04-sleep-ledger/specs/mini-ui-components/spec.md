## ADDED Requirements

### Requirement: Ledger pending and upcoming display

The system SHALL extend list/status components to support sleep ledger read-model statuses:

- **PENDING** (待登记): actionable pending registration state
- **UPCOMING** (未到期): future contract day, not yet registrable

`StatusIcon` and `StatusTag` MUST provide distinct visuals for PENDING and UPCOMING in addition to existing CreditEventType mappings.

#### Scenario: Pending ledger row

- **WHEN** LedgerItem receives statusType PENDING
- **THEN** StatusTag shows 待登记 with muted/pending styling

#### Scenario: Upcoming ledger row

- **WHEN** LedgerItem receives statusType UPCOMING
- **THEN** row appears disabled/non-interactive with 未到期 label

### Requirement: CreditEventType read-model extension

`constants/creditEventTypes.ts` SHALL define visual mappings for PENDING and UPCOMING used only for daily-view display, separate from append-only ledger event types FULFILLED and BREACH.

#### Scenario: Visual map completeness

- **WHEN** ledger page maps daily-view status to LedgerItem
- **THEN** PENDING and UPCOMING resolve to icons, labels, and colors via creditEventTypes
