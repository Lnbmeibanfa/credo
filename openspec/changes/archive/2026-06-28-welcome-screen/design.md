## Context

`mini-design-system` change 已交付完整组件库。当前 `pages/index/index.tsx` 为 Hello World 占位，设计稿 SCREEN 00 定义了欢迎页：品牌叙事、四象限概念教育、「开始立约」CTA。

本 change 用已有组件组装欢迎页，并为 CTA 提供跳转目标（contract-create placeholder）。

## Goals / Non-Goals

**Goals:**

- 还原 SCREEN 00 视觉与文案结构
- 使用 `PageShell`、`PageHeader`(brand)、`QuoteBlock`、`FeatureListCard`、`PrimaryButton`、`MonoLabel`
- 自定义导航栏，隐藏系统默认 navigationBar
- CTA 通过 `Taro.navigateTo` 跳转 contract-create placeholder
- 页面保持薄层，文案外置至 constants

**Non-Goals:**

- API / services 调用
- 登录、回访路由逻辑
- 完整创建契约表单
- 新增 design-system 组件（hero 字号用页面 scss 覆盖）

## Decisions

### 1. 路由：继续使用 `pages/index` 作为欢迎页

**选择**: 不新建 `pages/welcome`，直接改造 index。

**理由**: 小程序默认入口即 index；MVP 阶段欢迎页即首页。

### 2. 自定义导航栏

```typescript
// pages/index/index.config.ts
navigationStyle: 'custom'
```

**理由**: 设计稿顶栏为品牌自定义布局，非系统 title bar。

### 3. Hero 标题样式

使用 `SerifTitle` + 页面级 `.welcome-hero` scss 放大字号，不新增 HeroSection 组件。

**理由**: 仅 welcome 页使用，YAGNI。

### 4. CTA 跳转目标

跳转 `/pages/contract-create/index`，同 change 创建 placeholder 页（标题 + 占位文案）。

**理由**: 避免 CTA 无响应；完整表单留给后续 change。

### 5. 测试策略

纯 UI 页面，无 utils/services 逻辑，不要求单元测试或 snapshot。

**理由**: 符合 MVP testing-strategy 与 mini-design-system 先例。

## Risks / Trade-offs

| 风险 | 缓解 |
|------|------|
| 系统字体与设计稿衬线感有差距 | 已接受；保持字号层次 |
| placeholder 页用户体验不完整 | footer 标注 FIRST CONTRACT · 睡眠，明确下一步 |
| index 长期作为欢迎页 vs 未来 today 主页 | 后续 change 加 revisit 逻辑再调整路由 |

## Migration Plan

1. 更新 `pages/index` 三文件（tsx / scss / config）
2. 新建 `pages/contract-create` placeholder
3. 更新 `app.config.ts` pages 数组
4. 微信开发者工具目测对照设计稿

## Open Questions

（无 — 探索阶段已确认：index=欢迎页，CTA→placeholder，不做回访逻辑）
