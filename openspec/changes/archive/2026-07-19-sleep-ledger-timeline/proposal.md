## Why

用户完成睡眠契约签约后仍需手动进入账本，主流程断裂。账本页当前仅展示「待登记队列」（`status=PENDING`），登记完成后履约/未履约记录从列表消失，无法回顾截至今天的义务履行全貌。需要将账本默认视图改为「截至今天的时间线」，并在签约成功后自动进入账本。

## What Changes

- 契约创建/修改页：签约（含重新签约）成功后自动 `redirectTo` 睡眠账本页
- 账本页：默认请求 `daily-view` 时仅传 `to=<today>`，**不传** `status` 过滤，展示 PENDING / FULFILLED / BREACH
- 账本页：不展示今天之后的日期（通过 `to=today` 与后端日期范围截断实现）
- 账本页：列表排序保持最新日期在上；PENDING 可登记，FULFILLED/BREACH 只读
- 更新页面文案（subtitle、空状态）以匹配时间线语义，而非「待登记队列」
- **BREAKING（产品语义）**：撤销 `sleep-ledger-ux` 中「默认列表仅展示 PENDING」的要求

## Capabilities

### New Capabilities

（无 — 复用现有 API 与组件能力）

### Modified Capabilities

- `mini-sleep-contract-create`: 签约成功后 MUST 自动跳转睡眠账本页
- `mini-sleep-ledger`: 默认列表从「待登记队列」改为「截至今天的时间线」；更新 query helper 与空状态文案

## Non-goals

- 独立「历史记录」页面
- 限制时间线显示条数（如仅最近 7 天）
- 后端 API 或 schema 变更（`daily-view` 已有 `to` 与可选 `status`）
- 账本页返回按钮
- 登记后修改/删除记录
- 变更 summary 统计卡片行为

## Impact

- **Mini**: `pages/contract-create/index.tsx`, `pages/ledger/index.tsx`, `utils/ledgerView.ts`（新 timeline query helper），相关单元测试
- **Backend**: 无变更（验证现有 `daily-view` 行为即可）
- **OpenSpec**: delta specs for `mini-sleep-contract-create`, `mini-sleep-ledger`
