## ADDED Requirements

### Requirement: Global design tokens

The mini program SHALL define global design tokens for colors, typography, spacing, borders, and background decoration in `src/styles/tokens.scss`.

Tokens MUST include at minimum:

- Page background color (warm gray)
- Surface colors (card, muted footer, inverse)
- Text colors (primary, secondary, inverse, hint)
- Border colors (default, strong)
- Status semantic colors (fulfilled, breach, remedy, exempt, sign)
- Font family stacks using system fonts only (serif, sans, mono)
- Standard spacing scale (page padding, section gap, card padding)
- Dot-grid background mixin parameters

The system MUST NOT load external webfont files.

#### Scenario: Tokens available globally

- **WHEN** any component imports from `styles/tokens.scss`
- **THEN** all token variables and mixins are accessible without duplication

#### Scenario: System fonts only

- **WHEN** typography components render text
- **THEN** only system font stacks are used with no `@font-face` or webfont CDN

### Requirement: Typography scale

The design system SHALL provide typography mixins or utility classes for five layers used across MVP screens:

- MonoLabel (metadata: STEP 01, TODAY · date, EFFECTIVE)
- SerifTitle (page headings)
- SerifDisplay (large time/number displays such as 23:00)
- SansText (body and form labels)
- SerifSubtitle (hero slogans on welcome screen)

#### Scenario: Consistent heading hierarchy

- **WHEN** PageHeader renders a page title
- **THEN** it uses SerifTitle styling from the typography scale

#### Scenario: Time display formatting

- **WHEN** SerifDisplay renders a time value
- **THEN** it uses the designated display font size and serif font stack

### Requirement: Dot-grid page background

PageShell SHALL support an optional dot-grid background pattern matching the welcome and today screens.

#### Scenario: Dot grid enabled

- **WHEN** PageShell is rendered with `showDotGrid={true}`
- **THEN** the page background displays a subtle repeating dot pattern over the warm gray base color

#### Scenario: Dot grid disabled

- **WHEN** PageShell is rendered with `showDotGrid={false}`
- **THEN** only the solid page background color is shown

### Requirement: Safe area handling

PageShell SHALL apply safe-area insets for top and bottom padding on notched devices.

#### Scenario: Top safe area

- **WHEN** PageShell renders on a device with a notch
- **THEN** content does not overlap the status bar area

#### Scenario: Bottom safe area

- **WHEN** PageShell renders a full-width bottom button
- **THEN** bottom padding respects `env(safe-area-inset-bottom)`
