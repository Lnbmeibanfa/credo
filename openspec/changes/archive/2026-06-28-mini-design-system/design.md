## Context

Credo 小程序 MVP 包含 5 屏设计稿，视觉语言统一为「契约文书 · 信用账本」风格：衬线标题、等宽元数据、1px 直角边框、黑白灰主色。当前 `apps/mini/credo` 仅有 Taro 默认 Hello World，无样式系统和组件库。

本 change 仅交付设计系统基础层与可复用组件，页面组装留给后续独立 change。

## Goals / Non-Goals

**Goals:**

- 提取跨 5 屏一致的 Design Tokens 并实现为 Sass 变量 / mixin
- 按分层架构实现可复用 React 组件，覆盖 MVP 设计稿中的共用 UI 元素
- 定义 `CreditEventType` 枚举及视觉映射常量，供账本 / 复盘 / 状态标签复用
- 组件使用系统字体（PingFang SC / 系统衬线 fallback），不加载 webfont
- 倒计时组件支持客户端本地计算
- 复盘页「合理豁免」复用创建页的 `CheckboxGrid` 组件

**Non-Goals:**

- 完整页面实现与路由
- API 对接与 services 层
- check-in 业务逻辑
- UI snapshot 测试
- 自定义字体、第三方 UI 库

## Decisions

### 1. 样式方案：Sass Tokens + 组件 scoped scss

**选择**: `src/styles/tokens.scss` 定义 CSS 变量，`components/*/*.scss` 各组件独立样式。

**理由**: 与 Taro + Sass 模板一致，tokens 全局引入，组件样式隔离。

**备选**: CSS Modules — 增加配置复杂度，MVP 不必要。

### 2. 字体：系统字体栈

```scss
--font-serif: "Songti SC", "STSong", serif;
--font-sans: -apple-system, "PingFang SC", "Helvetica Neue", sans-serif;
--font-mono: "SF Mono", "Menlo", monospace;
```

**理由**: 用户明确要求系统字体；小程序 webfont 有体积和 FOUT 问题。

### 3. 组件目录分层

```
components/
├── layout/       PageShell, PageHeader, SectionBlock
├── typography/   MonoLabel, SerifTitle, SerifDisplay, SansText
├── surface/      Card, InfoBox, QuoteBlock, Divider
├── data-display/ StatGrid, SplitCompare, ContractCard, CountdownTimer, ContractBadge
├── form/         FormSection, TimePickerField, CheckboxGrid, OptionGrid,
│                 SegmentedControl, StatusSelector, RemedyList, TextAreaField
├── action/       PrimaryButton, SecondaryButton
├── list/         LedgerTimeline, LedgerItem, StatusTag, StatusIcon
└── brand/        BrandLogo, FeatureListCard
```

**理由**: 按职责分层，页面 change 按需 import，避免 flat 目录膨胀。

### 4. 倒计时：客户端本地计算

`CountdownTimer` 接收 `targetTime: string | Date`，组件内 `setInterval` 每秒更新剩余 HH : mm : ss。

**理由**: 用户确认本地计算；减少 API 依赖，今日主页纯展示场景足够。

### 5. 合理豁免：复用 CheckboxGrid

创建页与复盘页共用 `CheckboxGrid`，选项数据来自 `constants/exemptionOptions.ts`。

**理由**: 用户确认复用；domain 上豁免类型在缔约时已定义，复盘只是选择哪条豁免适用。

### 6. 组件测试策略

纯 UI 组件本 change 不要求 snapshot 测试。以下 utils 级逻辑需单元测试（若本 change 实现）：

- `utils/time.ts`: 倒计时格式化、时间字符串解析
- `constants/creditEventTypes.ts`: 类型 → 视觉属性映射

**理由**: 符合 Pragmatic TDD 与 MVP 测试策略；UI 组件靠后续页面集成验证。

### 7. 组件导出

每个子目录 `index.ts` barrel export；`components/index.ts` 统一 re-export 常用组件。

## Component API Summary

| 组件 | 关键 Props | 用于屏幕 |
|------|-----------|---------|
| PageShell | showDotGrid, children | 全部 |
| PageHeader | mode: brand/context, monoLabel, title, subtitle | 全部 |
| Card | variant, footer, children | 全部 |
| FormSection | index, title, hint, children | 创建、复盘 |
| TimePickerField | value, onChange | 创建 |
| CheckboxGrid | options, value, onChange, columns | 创建、复盘(豁免) |
| StatusSelector | options, value, onChange | 复盘 |
| OptionGrid | options, value, onChange, mode | 复盘 |
| SplitCompare | left, right, header | 创建、今日、复盘 |
| ContractCard | contractNo, status, targetBedtime, latestAllowed, todayStatus | 今日 |
| CountdownTimer | targetTime, label | 今日 |
| StatGrid | cells, motto | 账本 |
| LedgerItem | date, weekday, statusType, title, description | 账本 |
| PrimaryButton | children, icon, onClick, fullWidth | 全部 |

## Risks / Trade-offs

| 风险 | 缓解 |
|------|------|
| 系统衬线字体在不同设备差异大 | 接受 MVP 视觉偏差；保持 font-weight / size 层次一致 |
| 组件 API 与后续 API DTO 不对齐 | props 命名贴近 domain 字段（targetBedtime 等），页面 change 做薄映射 |
| TimePickerField 依赖 Taro Picker | 封装内部实现，外部只暴露 value/onChange |
| 组件数量多，首 change 工作量大 | tasks 分阶段：tokens → 原子 → 分子 → 复合 |

## Migration Plan

1. 新建 `styles/` 并在 `app.scss` 引入 tokens
2. 新建 `components/` 按分层逐步实现
3. 现有 `pages/index` 暂不改动（留给 welcome-screen change）
4. 无破坏性变更，纯增量

## Open Questions

（已解决）

- 字体 → 系统字体
- 豁免 UI → 复用 CheckboxGrid
- 倒计时 → 本地计算
- check-in → 不在本 change 范围
