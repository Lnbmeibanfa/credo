## Why

睡眠契约创建页使用自定义导航栏但缺少返回入口，用户进入后无法回到上级页面。睡眠账本页当前展示契约期内全部日期（含未来未到期、已履约、已违约），列表过长且信息噪音大，不符合「待登记队列」的使用场景。需要在不丢失统计概览的前提下，优化导航与账本列表的可读性。

## What Changes

- 契约创建/修改页（`contract-create`）增加返回上级入口（`PageHeader` 或等效组件）
- 后端 `GET /api/ledger/sleep/daily-view` 扩展查询参数：支持按日期范围（`from`/`to`，已有）及按状态过滤（`status`，可扩展多值）
- 账本页默认请求：仅返回 `recordDate ≤ today` 且 `status = PENDING` 的日条目；顶部 summary 统计卡片保留不变
- 账本页过滤结果为空时展示明确空状态文案（非报错）
- 前端 `services/ledger.ts` 透传查询参数，不在页面层做状态过滤

## Capabilities

### New Capabilities

- `backend-sleep-ledger-query`: 日视图 API 的状态过滤与日期边界查询能力，供前端按场景组合参数

### Modified Capabilities

- `mini-sleep-ledger`: 账本列表默认只展示待登记队列；空状态；通过 service 传参调用后端过滤
- `mini-sleep-contract-create`: 创建/修改契约页 MUST 提供返回上级入口
- `mini-ui-components`: `PageHeader` context 模式支持可选返回操作（`onBack`）

## Non-goals

- 账本「历史记录」独立页面（已履约/违约明细列表）
- 限制待登记 backlog 显示条数（如仅最近 7 天）
- 批量补记、登记后修改/删除
- 账本页返回按钮（本 change 仅覆盖契约页；账本页导航后续迭代）
- 后端 summary API 行为变更

## Impact

- **Backend**: `LedgerController`, `SleepLedgerService` — 新增 `status` 查询参数及过滤逻辑；单元/集成测试
- **Mini**: `PageHeader`, `contract-create/index.tsx`, `ledger/index.tsx`, `services/ledger.ts`, `utils/ledgerView.ts`（参数构建/helper，非列表过滤）
- **OpenSpec**: 本 change 的 delta specs；依赖已实现的 `sleep-ledger` / `sleep-contract-create` 基础能力
