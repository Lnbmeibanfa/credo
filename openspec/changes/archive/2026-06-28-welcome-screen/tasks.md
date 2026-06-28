## 1. Content & Config

- [x] 1.1 Write unit test for `constants/welcomeContent.ts` (required keys: heroLines, quoteLines, ctaLabel, footerLabel)
- [x] 1.2 Create `constants/welcomeContent.ts` with all welcome screen copy
- [x] 1.3 Update `pages/index/index.config.ts` with `navigationStyle: 'custom'`

## 2. Welcome Page UI

- [x] 2.1 Implement `pages/index/index.scss` with hero typography and section spacing
- [x] 2.2 Implement `pages/index/index.tsx` assembling PageShell, PageHeader, QuoteBlock, FeatureListCard, PrimaryButton, MonoLabel
- [x] 2.3 Wire CTA `onClick` to `Taro.navigateTo` contract-create page

## 3. Contract Create Placeholder

- [x] 3.1 Create `pages/contract-create/index.tsx`, `index.scss`, `index.config.ts` as placeholder
- [x] 3.2 Register `pages/contract-create/index` in `app.config.ts`

## 4. Verification

- [x] 4.1 Run `pnpm test`, `tsc --noEmit`, and `eslint` in `apps/mini/credo`
- [x] 4.2 Visual check in WeChat DevTools against SCREEN 00 design
