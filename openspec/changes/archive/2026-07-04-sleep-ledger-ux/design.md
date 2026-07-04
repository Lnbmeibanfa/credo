## Context

睡眠契约创建页（`contract-create`）使用 `navigationStyle: 'custom'`，系统返回按钮不可用，且 `PageHeader` 无返回操作。睡眠账本页通过 `GET /api/ledger/sleep/daily-view` 获取契约全周期日列表，前端全量渲染四种状态（PENDING / UPCOMING / FULFILLED / BREACH），导致列表过长。

后端 `daily-view` 已支持 `from` / `to` 日期参数，但无状态过滤。用户决策：summary 统计保留；空状态需明确；backlog 长度暂不限；**过滤在后端**，前端通过传参组合查询，保留扩展性（未来历史页可传 `status=FULFILLED,BREACH`）。

## Goals / Non-Goals

**Goals:**

- 契约创建/修改页提供返回上级入口
- `daily-view` API 支持 `status` 查询参数（可组合、可扩展）
- 账本页默认只展示「今天及以前 + PENDING」的待登记队列
- 过滤结果为空时展示友好空状态
- 顶部 summary 卡片行为不变
- 遵循 Pragmatic TDD：后端 service 测试先行

**Non-Goals:**

- 历史记录独立页面
- 限制 backlog 显示条数
- 账本页返回按钮
- summary API 变更
- Flyway 迁移（无 schema 变更）

## Decisions

### 1. 后端 `status` 参数设计

**选择：** `GET /api/ledger/sleep/daily-view?status=PENDING&to=2026-07-04`（`status` 为逗号分隔枚举，可选多值）

**允许值：** `PENDING`, `UPCOMING`, `FULFILLED`, `BREACH`（与读模型 status 一致）

**行为：**

1. 仍先按契约范围 + `from`/`to` 生成完整日列表（现有 `buildDailyDays` 逻辑）
2. 若 `status` 未传，返回全部（**向后兼容**）
3. 若 `status` 传入，过滤后返回子集
4. `to` 未传时默认 `contract.endDate`；账本页传 `to=today` 排除未来

**备选：** 前端 filter — 用户明确要求后端过滤，弃用。

### 2. 默认 `to` 与未来的关系

当 `to=today` 且 `status` 含 `UPCOMING` 时，UPCOMING 日自然不在 `to` 范围内，无需额外逻辑。  
当 `status=PENDING&to=today` 时，等价于「待登记队列」。

### 3. 返回入口实现

**选择：** 扩展 `PageHeader` context 模式，新增可选 `onBack?: () => void`；契约页传入 `() => Taro.navigateBack()`。

**备选：** 恢复系统导航栏 — 与 custom nav 设计冲突，弃用。

### 4. 账本页数据加载

**选择：** 单次 `getSleepDailyView({ status: 'PENDING', to: todayISO })`；summary 仍来自 `view.summary`（后端 daily-view 响应内嵌 summary，保持不变）。

若 summary 与过滤列表不一致：summary 始终反映全契约统计（obligation/pending/fulfilled/breach），列表仅为 PENDING 子集 — 符合「保留统计、列表是队列」的产品语义。

### 5. 空状态

当 `days.length === 0` 且已有契约时，展示：

> 「暂无待登记日期」+ 简短说明（如「当前没有需要登记的睡眠记录，继续保持。」）

区别于「无契约」空状态（已有实现）。

### 6. 前端参数构建

在 `utils/ledgerView.ts` 新增 `buildPendingQueueQuery(today?: string)` 返回 `{ status: 'PENDING', to: today }`，供页面/service 使用；**不在 utils 做列表 filter**。

## Risks / Trade-offs

| 风险 | 缓解 |
|------|------|
| `status` 多值解析错误 | 单元测试覆盖非法值 → 400 |
| 无 `status` 时行为变化 | 未传参保持全量返回，向后兼容 |
| summary 与列表数字不一致使用户困惑 | 列表标题/subtitle 说明「以下为待登记」；统计卡片保留全量 |
| `navigateBack` 无栈可退（深链进入） | MVP 接受；可选 fallback `redirectTo` 首页（实现时加栈深判断） |

## Migration Plan

1. 后端：扩展 controller/service + 测试，部署无 migration
2. 前端：PageHeader → contract-create → ledger service/页面
3. 无数据迁移；旧客户端未传 `status` 行为不变

## Open Questions

- （已决）过滤放后端 — 是
- （已决）历史页 — 暂不做，API 已预留 `status` 参数
