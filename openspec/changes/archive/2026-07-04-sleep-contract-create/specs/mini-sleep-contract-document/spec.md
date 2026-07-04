## ADDED Requirements

### Requirement: Sleep contract document component

The system SHALL provide `SleepContractDocument` component rendering the formal sleep contract document matching SCREEN 01 design.

The component MUST display:

- Header: PERSONAL SLEEP CONTRACT / 睡眠契约
- Contract number (when available)
- Effective start and end dates in split layout
- Article 1: contract content with targetBedtime and date range
- Article 2: enabled breach clauses as numbered list
- Article 3: irrevocable clause (fixed template text)
- Party A (promisor) and Party B (supervisor) — MVP both show「本人」
- Seal area: `preview` mode shows「待签」; `signed` mode shows signed timestamp

#### Scenario: Preview mode before sign

- **WHEN** mode is preview and user has not submitted
- **THEN** seal shows 待签 and no SIGNED AT footer

#### Scenario: Signed mode after API success

- **WHEN** mode is signed with signedAt and contractNo
- **THEN** footer shows SIGNED AT and contract number

### Requirement: Document reflects form props

SleepContractDocument MUST be a controlled component receiving all display data via props derived from form state.

It MUST NOT maintain independent copy of bedtime or dates.

#### Scenario: Props drive article content

- **WHEN** targetBedtime prop changes
- **THEN** Article 1 promise text updates on next render

#### Scenario: Breach clauses in article 2

- **WHEN** REVIEW is disabled in props
- **THEN** Article 2 list excludes review item

### Requirement: Document scroll placement

On contract-create page, SleepContractDocument SHALL appear below form sections and above the confirm sign button, scrollable within page.

#### Scenario: Document visible while editing

- **WHEN** user scrolls down on create page
- **THEN** document preview and confirm button are reachable
