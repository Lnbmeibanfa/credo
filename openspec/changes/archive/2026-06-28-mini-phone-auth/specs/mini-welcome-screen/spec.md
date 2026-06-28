## MODIFIED Requirements

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
