## ADDED Requirements

### Requirement: Contract create page back navigation

The contract-create page MUST provide a visible back action to return to the previous page when entered via navigation stack (e.g. from welcome/index).

The back action MUST call `Taro.navigateBack()` when a previous page exists in the stack.

The page uses custom navigation (`navigationStyle: 'custom'`); therefore the back control MUST be rendered in page UI (via `PageHeader` or equivalent), not rely on the system navigation bar.

#### Scenario: Back from create flow

- **WHEN** user navigates from index to contract-create and taps back
- **THEN** user returns to index page

#### Scenario: Back during edit flow

- **WHEN** user opens existing contract for edit and taps back
- **THEN** user returns to the page that opened contract-create
