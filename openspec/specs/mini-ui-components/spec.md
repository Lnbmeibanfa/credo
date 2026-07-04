### Requirement: Component library structure

The mini program SHALL organize reusable UI components under `src/components/` with subdirectories: layout, typography, surface, data-display, form, action, list, and brand.

Each component MUST have its own directory containing at minimum a TSX file and a scoped SCSS file.

#### Scenario: Component discoverability

- **WHEN** a page change needs a Card component
- **THEN** it can import from `components/surface/Card` or the barrel export

### Requirement: Layout components

The system SHALL provide layout components:

- **PageShell**: page wrapper with background, safe-area, optional dot-grid
- **PageHeader**: two modes — `brand` (logo + title + version) and `context` (mono label + serif title + subtitle + optional right label, optional back action via `onBack`)
- **SectionBlock**: mono label + optional title block wrapping children

When `PageHeader` context mode receives `onBack`, it MUST render a tappable back control (e.g. arrow + 「返回」) that invokes the callback.

#### Scenario: Brand header on welcome screen

- **WHEN** PageHeader is rendered with `mode="brand"`
- **THEN** it displays BrandLogo, brand title, and mono version label

#### Scenario: Context header on business screens

- **WHEN** PageHeader is rendered with `mode="context"` and monoLabel "TODAY · 2026.06.27"
- **THEN** it displays the mono label, serif title, and optional subtitle

#### Scenario: Context header with back action

- **WHEN** PageHeader is rendered with `mode="context"` and `onBack` callback
- **THEN** a back control is visible and tapping it invokes `onBack`

### Requirement: Surface components

The system SHALL provide surface components:

- **Card**: white background, 1px border, optional muted footer slot
- **InfoBox**: muted background box with optional icon slot for descriptive text
- **QuoteBlock**: left vertical bar with multi-line quote text
- **Divider**: 1px horizontal separator

#### Scenario: Card with footer

- **WHEN** Card receives a footer prop (e.g. today status bar)
- **THEN** the footer renders in a muted background section below the card body

### Requirement: Action components

The system SHALL provide:

- **PrimaryButton**: full-width black background, white text, optional icon, optional letter-spacing
- **SecondaryButton**: full-width white background, black border and text, optional icon

#### Scenario: Primary CTA styling

- **WHEN** PrimaryButton renders with label "开始立约"
- **THEN** it displays as a full-width black rectangle with white centered text

#### Scenario: Secondary action styling

- **WHEN** SecondaryButton renders with label "查看信用记录"
- **THEN** it displays as a full-width outlined button with black border

### Requirement: Form components

The system SHALL provide form components:

- **FormSection**: numbered section header (index · title) with optional hint text
- **TimePickerField**: large bordered time display triggering Taro time picker; exposes value/onChange as "HH:mm" string
- **DateRangeField**: inclusive start/end date pickers at day granularity with total days display (共 N 天)
- **BreachClauseSelector**: sleep contract breach clause configuration with mandatory RECORD row
- **CheckboxGrid**: multi-select grid with check-icon style (used for exemption conditions)
- **OptionGrid**: selectable grid supporting single or multiple mode with inverse selected style
- **SegmentedControl**: binary or small-set toggle (e.g. 是/否)
- **StatusSelector**: equal-width large square buttons for fulfilled / breach / exempt states; exempt variant uses gold accent border
- **RemedyList**: numbered list of remedy plan items
- **TextAreaField**: bordered multi-line text input

CheckboxGrid MUST be reusable for both contract creation (exemption rules) and daily review (reasonable exemption selection).

#### Scenario: Time picker interaction

- **WHEN** user taps TimePickerField showing "23:00"
- **THEN** the Taro time picker opens and onConfirm updates the displayed value

#### Scenario: Exemption grid reuse

- **WHEN** CheckboxGrid is used on contract creation and daily review screens
- **THEN** both screens use the same component with options from shared constants

#### Scenario: Status selector three states

- **WHEN** StatusSelector renders three options (已履约, 未履约, 合理豁免)
- **THEN** the selected option displays inverse (black background, white text) except exempt which uses gold border accent

### Requirement: Data display components

The system SHALL provide data display components:

- **SplitCompare**: two-column label + serif value comparison with optional header slot
- **ContractCard**: specialized card showing contract number, status badge, target/latest times, today status footer
- **ContractBadge**: contract number + status mono label (e.g. "睡眠契约 · No. 001" + "EFFECTIVE")
- **CountdownTimer**: displays remaining time as "HH : mm : ss" computed locally from a target time
- **StatGrid**: horizontal row of stat cells with optional motto text below
- **StatCell**: single stat with label, value, and optional unit
- **SleepContractDocument**: formal sleep contract document with preview and signed modes

CountdownTimer MUST compute remaining time on the client using setInterval without requiring a server-provided countdown value.

#### Scenario: Local countdown update

- **WHEN** CountdownTimer receives targetTime and current time is before target
- **THEN** it displays decrementing hours, minutes, and seconds updated every second

#### Scenario: Countdown expired

- **WHEN** current time passes targetTime
- **THEN** CountdownTimer displays "00 : 00 : 00" or a zero state

#### Scenario: Contract card layout

- **WHEN** ContractCard renders with targetBedtime "23:00" and latestAllowed "23:30"
- **THEN** it displays a SplitCompare layout with status badge header and today status footer

### Requirement: List and status components

The system SHALL provide list components for the credit ledger:

- **LedgerTimeline**: vertical list container
- **LedgerItem**: single ledger row with date, weekday, status icon, title, status tag, description
- **StatusTag**: colored tag pill mapped from CreditEventType
- **StatusIcon**: square icon box with character (S, +, -, R, ○) mapped from CreditEventType

CreditEventType enum MUST be defined in `constants/creditEventTypes.ts` with visual mapping for: SIGN, FULFILLED, BREACH, REMEDY, EXEMPT, PENDING, UPCOMING.

#### Scenario: Fulfilled ledger entry

- **WHEN** LedgerItem receives statusType FULFILLED
- **THEN** StatusIcon shows "+" and StatusTag renders in fulfilled green color

#### Scenario: Breach ledger entry

- **WHEN** LedgerItem receives statusType BREACH
- **THEN** StatusIcon shows "-" and StatusTag renders in breach color

### Requirement: Brand components

The system SHALL provide brand components for the welcome screen:

- **BrandLogo**: square border box containing the character "立"
- **FeatureListCard**: four-row read-only list (承诺, 履约, 未履约, 补救) with label and description columns

#### Scenario: Feature list display

- **WHEN** FeatureListCard renders
- **THEN** it displays four non-interactive rows with Chinese labels and descriptions matching the welcome screen design

### Requirement: Time utility functions

The system SHALL provide `utils/time.ts` with pure functions for:

- Formatting time strings for display
- Computing remaining seconds until a target time
- Formatting seconds as "HH : mm : ss" countdown string

These functions MUST have unit tests.

#### Scenario: Countdown formatting

- **WHEN** formatCountdown(10000) is called with 10000 seconds remaining
- **THEN** it returns "02 : 46 : 40"

#### Scenario: Time parsing

- **WHEN** parseTimeString("23:30") is called
- **THEN** it returns a valid Date or time object for local calculation

### Requirement: Shared constants

The system SHALL define shared constants:

- `constants/creditEventTypes.ts`: CreditEventType enum + visual properties (icon char, tag color, label)
- `constants/exemptionOptions.ts`: exemption condition options for CheckboxGrid
- `constants/breachReasons.ts`: breach reason options for OptionGrid on review screen

#### Scenario: Exemption options consistency

- **WHEN** contract creation and daily review both import exemptionOptions
- **THEN** they display identical option labels and ids

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

The system SHALL provide `SleepContractDocument` in `components/data-display/` rendering formal contract document with preview and signed modes.

#### Scenario: Component export

- **WHEN** contract-create page imports document component
- **THEN** it is available from components barrel export

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
