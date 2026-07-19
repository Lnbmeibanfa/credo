## ADDED Requirements

### Requirement: Post-sign navigation to sleep ledger

After a successful contract upsert (initial sign or re-sign), the contract-create page MUST navigate the user to the sleep ledger page using `Taro.redirectTo`.

The navigation MUST occur after the success feedback (toast) is shown or immediately upon success.

The page MUST NOT require the user to manually tap a secondary link to reach the ledger after signing.

#### Scenario: Initial sign redirects to ledger

- **WHEN** user completes first-time sign with valid form and PUT succeeds
- **THEN** user is redirected to sleep ledger page

#### Scenario: Re-sign redirects to ledger

- **WHEN** user modifies an existing contract and confirms re-sign successfully
- **THEN** user is redirected to sleep ledger page
