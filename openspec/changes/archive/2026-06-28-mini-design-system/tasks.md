## 1. Design Tokens & Global Styles

- [x] 1.1 Create `src/styles/tokens.scss` with color, typography, spacing, border, and status semantic tokens (system fonts only)
- [x] 1.2 Create `src/styles/mixins.scss` with dot-grid background and quote-bar mixins
- [x] 1.3 Create `src/styles/typography.scss` with MonoLabel, SerifTitle, SerifDisplay, SansText, SerifSubtitle mixins
- [x] 1.4 Create `src/styles/index.scss` and import tokens/mixins/typography in `app.scss`

## 2. Constants & Utilities

- [x] 2.1 Write unit tests for `utils/time.ts` (formatCountdown, parseTimeString, getRemainingSeconds)
- [x] 2.2 Implement `utils/time.ts` pure functions for countdown and time parsing
- [x] 2.3 Write unit tests for `constants/creditEventTypes.ts` visual mapping (icon, color, label per type)
- [x] 2.4 Implement `constants/creditEventTypes.ts` with CreditEventType enum and visual properties
- [x] 2.5 Implement `constants/exemptionOptions.ts` shared exemption options for CheckboxGrid
- [x] 2.6 Implement `constants/breachReasons.ts` breach reason options for OptionGrid

## 3. Typography Components

- [x] 3.1 Implement `components/typography/MonoLabel`
- [x] 3.2 Implement `components/typography/SerifTitle`
- [x] 3.3 Implement `components/typography/SerifDisplay`
- [x] 3.4 Implement `components/typography/SansText`
- [x] 3.5 Create `components/typography/index.ts` barrel export

## 4. Layout Components

- [x] 4.1 Implement `components/layout/PageShell` with dot-grid, safe-area, and background
- [x] 4.2 Implement `components/layout/PageHeader` with brand and context modes
- [x] 4.3 Implement `components/layout/SectionBlock`
- [x] 4.4 Create `components/layout/index.ts` barrel export

## 5. Surface Components

- [x] 5.1 Implement `components/surface/Card` with optional muted footer
- [x] 5.2 Implement `components/surface/InfoBox` with icon slot
- [x] 5.3 Implement `components/surface/QuoteBlock`
- [x] 5.4 Implement `components/surface/Divider`
- [x] 5.5 Create `components/surface/index.ts` barrel export

## 6. Action Components

- [x] 6.1 Implement `components/action/PrimaryButton` (full-width, icon, letter-spacing)
- [x] 6.2 Implement `components/action/SecondaryButton`
- [x] 6.3 Create `components/action/index.ts` barrel export

## 7. Form Components

- [x] 7.1 Implement `components/form/FormSection` (index · title + hint)
- [x] 7.2 Implement `components/form/TimePickerField` wrapping Taro time picker
- [x] 7.3 Implement `components/form/CheckboxGrid` (multi-select, check-icon style)
- [x] 7.4 Implement `components/form/OptionGrid` (single/multiple, inverse selected)
- [x] 7.5 Implement `components/form/SegmentedControl`
- [x] 7.6 Implement `components/form/StatusSelector` (fulfilled/breach/exempt with gold accent)
- [x] 7.7 Implement `components/form/RemedyList`
- [x] 7.8 Implement `components/form/TextAreaField`
- [x] 7.9 Create `components/form/index.ts` barrel export

## 8. Data Display Components

- [x] 8.1 Implement `components/data-display/SplitCompare` (two-column label + value)
- [x] 8.2 Implement `components/data-display/ContractBadge`
- [x] 8.3 Implement `components/data-display/ContractCard` (uses SplitCompare + Card footer)
- [x] 8.4 Implement `components/data-display/CountdownTimer` with local setInterval computation
- [x] 8.5 Implement `components/data-display/StatCell` and `StatGrid`
- [x] 8.6 Create `components/data-display/index.ts` barrel export

## 9. List & Status Components

- [x] 9.1 Implement `components/list/StatusIcon` mapped from CreditEventType
- [x] 9.2 Implement `components/list/StatusTag` mapped from CreditEventType
- [x] 9.3 Implement `components/list/LedgerItem`
- [x] 9.4 Implement `components/list/LedgerTimeline`
- [x] 9.5 Create `components/list/index.ts` barrel export

## 10. Brand Components

- [x] 10.1 Implement `components/brand/BrandLogo`
- [x] 10.2 Implement `components/brand/FeatureListCard`
- [x] 10.3 Create `components/brand/index.ts` barrel export

## 11. Integration & Tooling

- [x] 11.1 Create top-level `components/index.ts` re-exporting all component groups
- [x] 11.2 Add vitest to `apps/mini/credo` and configure `npm test` script
- [x] 11.3 Verify `tsc --noEmit` and `eslint` pass with new component files
