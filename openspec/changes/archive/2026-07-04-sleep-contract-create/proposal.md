## Why

用户完成欢迎页登录后，核心 MVP 流程是「创建睡眠契约」——将自律目标转化为可执行的合约并写入信用账户。当前 `contract-create` 仅为占位页，缺少表单、文书预览与后端持久化，无法进入后续履约/复盘链路。

## What Changes

- Flyway V3：通用 `contract` 主表 + `sleep_contract` 扩展表 + `contract_breach_clause` 违约条款实例表
- 后端：睡眠契约创建/更新 API（一人一契约，可修改）、契约编号生成、违约条款快照
- 小程序：SCREEN 01 创建页（入眠时间、生效日期区间、违约条款选择）
- 小程序：新建 `SleepContractDocument` 文书组件，与表单双向绑定实时预览
- 小程序：新建 `DateRangeField` 日期区间组件（粒度：天）
- 小程序：违约条款选择 UI（记录必选锁定、复盘可选、自定义可编辑）
- 小程序：`services/contract.ts` 封装 create/update API
- 确认签约时统一调用 API（非分步 draft API）

## Capabilities

### New Capabilities

- `backend-sleep-contract`: 契约表结构、创建/更新 API、一人一契约约束、违约条款持久化
- `mini-sleep-contract-create`: 创建页表单、DateRangeField、BreachClauseSelector、与 API 集成
- `mini-sleep-contract-document`: 睡眠契约文书预览/已签展示组件（双向绑定）

### Modified Capabilities

- `mini-ui-components`: 新增 DateRangeField、BreachClauseSelector、SleepContractDocument 组件要求

## Non-goals

- 多合约并行管理（一人同时多份 ACTIVE 合约）
- 邀请他人作为监督人（MVP 甲乙方均为本人）
- 合约草稿服务端持久化（表单状态仅前端，提交时一次性 API）
- `latest_allowed` 入眠宽限时间（MVP 仅 `target_bedtime`）
- 今日首页 / 履约判定 / 复盘流程（后续 change）
- Web 端契约管理

## Impact

- **Backend**: `services/credo-server` — Flyway V3、entity/mapper/service/controller、单元测试
- **Mini**: `apps/mini/credo` — `pages/contract-create/`、新组件、`services/contract.ts`
- **Depends on**: 用户登录（JWT）、设计系统组件（TimePickerField、FormSection 等）
- **OpenSpec**: 扩展 `mini-ui-components` delta spec
