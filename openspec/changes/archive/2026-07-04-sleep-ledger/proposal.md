## Why

睡眠契约（V3）只存契约定义，没有按日履约记录，无法支撑「签了就要每天登记」的产品语义，也无法做永久时间维度统计。用户需要在契约生效期内逐日登记履约或未履约，漏登记的日子显示待登记（不自动判违约），并支持事后逐条补记。

## What Changes

- Flyway V4：`sleep_ledger_event` 表，仅存用户已确认的事实（FULFILLED / BREACH），append-only
- 后端：单日登记 API（一次一条，无批量补记）、日视图查询 API（合并契约日历 + 账本，计算待登记 / 未到期）
- 后端：登记校验（契约生效期内、record_date ≤ 今天、不可重复、不可改删）
- 小程序：`services/ledger.ts` 封装登记与查询
- 小程序：睡眠账本页（或今日页 MVP）— 展示日列表、待登记高亮、单次登记履约/违约
- 小程序：复用 `LedgerItem` / `LedgerTimeline` 展示已登记与待登记状态
- 扩展 `CreditEventType` 读模型：`PENDING`（待登记）、`UPCOMING`（未到期）仅用于 UI，不入库

## Capabilities

### New Capabilities

- `backend-sleep-ledger`: 账本表结构、单日登记 API、日视图查询、统计聚合基础
- `mini-sleep-ledger`: 账本页 UI、单次登记交互、待登记/未到期展示、与 ledger service 集成

### Modified Capabilities

- `mini-ui-components`: LedgerItem / LedgerTimeline 支持待登记、未到期展示态（delta spec）

## Non-goals

- 批量补记（一次提交多条记录）
- 自动判定违约（超时未登记自动变 BREACH）
- 登记后修改或删除记录
- REMEDY / EXEMPT 事件类型（后续 change）
- 推送提醒、今日首页完整 SCREEN（可后续迭代；本 change 聚焦账本登记链路）
- 客观上床时间采集、信用分/排名/社区
- Web 端账本管理

## Impact

- **Backend**: `services/credo-server` — Flyway V4、entity/mapper/service/controller、单元测试
- **Mini**: `apps/mini/credo` — 新页面（账本/登记）、`services/ledger.ts`、`constants/creditEventTypes.ts` 读模型扩展
- **Depends on**: JWT 鉴权、已有睡眠契约（`sleep-contract-create` / V3 表）
- **OpenSpec**: 新增 backend/mini sleep-ledger specs；`mini-ui-components` delta
