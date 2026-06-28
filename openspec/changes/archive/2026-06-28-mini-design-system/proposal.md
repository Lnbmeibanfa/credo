## Why

Credo MVP 小程序已有 5 屏高保真设计稿（欢迎、创建契约、今日主页、日复盘、信用账本），但当前代码仍是 Taro 默认模板，缺少统一的设计语言和可复用组件。若不先建立设计系统与组件库，后续各页面 change 将重复实现样式、交互不一致，维护成本陡增。

## What Changes

- 建立 Credo 小程序 Design Tokens（颜色、字体、间距、边框），使用**系统字体**，不引入 webfont
- 创建分层组件库：layout / typography / surface / data-display / form / action / list / brand
- 实现跨 5 屏复用的核心组件（PageShell、Card、FormSection、SplitCompare、StatusSelector、LedgerItem 等）
- 建立 `styles/` 全局样式入口与 `constants/` 信用事件类型映射
- 本 change **不包含**完整页面组装、API 对接、路由 wiring

## Non-goals

- 不实现完整 5 个业务页面（welcome / contract-create / today / review / ledger 各为独立 change）
- 不对接后端 API 或 `services/` 业务逻辑
- 不实现「我准备睡觉了」check-in 流程
- 不引入自定义 webfont 或 UI 第三方组件库
- 不做 UI snapshot 测试（MVP 规范允许纯 UI 组件跳过 snapshot）
- 不涉及 web 端（apps/web）

## Capabilities

### New Capabilities

- `mini-design-tokens`: 全局 design tokens、typography mixin、点阵背景等基础样式
- `mini-ui-components`: 可复用 UI 组件库，覆盖 MVP 五屏设计稿中的共用元素

### Modified Capabilities

（无现有 spec）

## Impact

- **Affected**: `apps/mini/credo/src/styles/`（新建）、`apps/mini/credo/src/components/`（新建）、`apps/mini/credo/src/constants/`（新建）、`apps/mini/credo/src/app.scss`
- **Not affected**: 后端 services、credo-server、页面路由、API 层
- **Dependencies**: 无新增 npm 依赖；继续使用 Taro 4.2 + React + Sass
