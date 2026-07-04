## MODIFIED Requirements

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
