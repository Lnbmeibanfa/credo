## Context

`sleep-ledger-ux`（已归档）将账本默认视图定为「待登记队列」：前端传 `status=PENDING&to=today`，登记完成后条目从列表消失。用户反馈需要回顾截至今天的履约全貌，且签约后应直接进入账本完成登记。

后端 `GET /api/ledger/sleep/daily-view` 已支持 `from`/`to` 与可选 `status`；当 `status` 省略且 `to=today` 时，返回契约起始日至今天所有日条目（PENDING / FULFILLED / BREACH），不含未来 UPCOMING。无需后端或 schema 变更。

## Goals / Non-Goals

**Goals:**

- 签约（含重新签约）成功后 `Taro.redirectTo` 睡眠账本页
- 账本默认请求 `{ to: today }`，不传 `status`
- 列表展示截至今天所有状态；PENDING 可登记，FULFILLED/BREACH 只读
- 列表排序：最新日期在上（沿用 `sortDaysForDisplay`）
- 更新 subtitle / 空状态文案
- 前端 `utils/ledgerView.ts` 新 timeline query helper + 单元测试

**Non-Goals:**

- 后端 API 变更
- 独立历史页、条数限制、summary 变更
- 移除 `buildPendingQueueQuery` 的向后兼容（可保留函数供未来场景，但账本页不再使用）

## Decisions

### 1. 签约后导航方式

**选择：** `Taro.redirectTo({ url: '/pages/ledger/index' })`，在 toast「签约成功」之后执行（短 delay 可选，或直接 redirect）。

**理由：** 用户确认签约 → 登记是主流程；清栈避免返回契约页误操作。

**备选：** `navigateTo` — 保留返回契约页能力；用户已确认不需要。

### 2. 账本默认查询参数

**选择：** 新增 `buildLedgerTimelineQuery(today?)` → `{ to: today ?? formatIsoDate(new Date()) }`，不传 `status`。

**理由：** 后端 `filterDaysByStatus` 在 `status` 为空时返回全状态子集；`to=today` 截断未来日期。

**备选：** `status=PENDING,FULFILLED,BREACH` 显式枚举 — 冗余，且与后端「省略 status」语义重复。

### 3. 空状态文案

**选择：** 当 `days.length === 0` 且有契约时，展示「暂无义务日期」类文案（例如契约尚未开始、startDate 在未来）。

**理由：** 时间线视图下「全部登记完」不会出现空列表（已登记日仍展示）；空列表仅见于边界情况。

### 4. 保留 `buildPendingQueueQuery`

**选择：** 保留函数与测试，账本页改用 `buildLedgerTimelineQuery`；不在本 change 删除 pending helper。

**理由：** API 层仍支持 status 过滤，helper 可供未来「仅看待登记」视图复用。

## Risks / Trade-offs

- **[Risk] 长契约列表过长** → MVP 全量展示；后续可按 from 窗口分页，非本 change 范围
- **[Risk] redirectTo 后无法返回修改契约** → 用户仍可从首页「开始立约」进入 contract-create 编辑
- **[Risk] Spec 与 sleep-ledger-ux 归档语义冲突** → 本 change delta 明确 MODIFIED/REMOVED，归档时合并入主 spec

## Migration Plan

1. 实现并跑通 `pnpm test`、`tsc`（mini）
2. 无需后端部署依赖；前后端可独立发布
3. 回滚：恢复 ledger 页使用 `buildPendingQueueQuery`、移除签约 redirect

## Open Questions

（无 — 用户已确认 redirectTo 与最新日期在上排序）
