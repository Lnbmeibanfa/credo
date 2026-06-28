### Requirement: Welcome screen brand header

The welcome screen SHALL display a brand header with logo, title "立约 · Contract", and mono label "PERSONAL CREDIT · v0.1" using PageHeader in brand mode.

#### Scenario: Brand header visible on load

- **WHEN** user opens the mini program index page
- **THEN** the brand logo, title, and version label are displayed at the top

### Requirement: Welcome screen hero and product positioning

The welcome screen SHALL display two hero slogan lines and a quote block explaining the product is a personal contract and credit management system, not a habit tracker.

#### Scenario: Hero slogans displayed

- **WHEN** the welcome screen renders
- **THEN** the hero section shows "别再依靠热情许愿。" and "开始与自己签订契约。"

#### Scenario: Product positioning quote

- **WHEN** the welcome screen renders
- **THEN** QuoteBlock displays the two-line product definition text

### Requirement: Welcome screen concept education

The welcome screen SHALL display FeatureListCard with four read-only rows: 承诺, 履约, 未履约, 补救.

#### Scenario: Four concept rows

- **WHEN** the welcome screen renders
- **THEN** FeatureListCard shows four non-interactive concept rows with Chinese labels and descriptions

### Requirement: Welcome screen call to action

The welcome screen SHALL display a primary CTA button labeled「开始立约 →」and a footer mono label「FIRST CONTRACT · 睡眠」.

The CTA MUST only navigate to contract-create when the user has completed phone authorization login and holds a valid stored auth token. When unauthenticated, the page MUST prompt phone authorization first.

#### Scenario: CTA button styling

- **WHEN** PrimaryButton renders with label「开始立约」
- **THEN** it displays as a full-width black rectangle with white centered text

#### Scenario: CTA navigates to contract create when authenticated

- **WHEN** authenticated user taps the「开始立约」button
- **THEN** the app navigates to the contract-create page

#### Scenario: CTA blocked when unauthenticated

- **WHEN** user without stored token attempts to start contract
- **THEN** the app does not navigate until phone authorization login succeeds

### Requirement: Custom navigation bar

The welcome screen SHALL use custom navigation style and hide the default WeChat navigation bar title.

#### Scenario: Custom nav enabled

- **WHEN** the index page config is loaded
- **THEN** navigationStyle is set to custom

### Requirement: Dot-grid page background

The welcome screen SHALL use PageShell with dot-grid background enabled.

#### Scenario: Background pattern

- **WHEN** the welcome screen renders
- **THEN** PageShell displays dot-grid background over warm gray base color
