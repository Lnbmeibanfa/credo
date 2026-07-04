## Context

V3 已落地睡眠契约（`contract` + `sleep_contract` + `contract_breach_clause`），用户可创建/更新契约，但无按日履约数据。前端已有 `CreditEventType`、`LedgerItem`、`LedgerTimeline` 等展示组件，尚无后端数据源。

产品决策（探索阶段已确认）：

1. **睡眠日**：按目标 bedtime 所在自然日计（例 6/28 23:00 → record_date = 2026-06-28）
2. **逐日义务**：契约生效期内每个已到期日都应收尾登记；未登记显示**待登记**，不自动判违约
3. **补记**：用户可事后对过去未登记日逐条补记（一次一条，无批量 API）
4. **未到期**：record_date > 今天的契约内日期显示**未到期**，不可登记
5. **账本 immutability**：仅 INSERT FULFILLED/BREACH；禁止 UPDATE/DELETE

## Goals / Non-Goals

**Goals:**

- V4 Flyway：`sleep_ledger_event` append-only 表
- 单日登记 API + 日视图查询 API（虚拟待登记/未到期）
- 小程序账本页：列表展示、单次登记、待登记引导
- 统计基础：义务天数、已登记、待登记、履约、违约（查询时聚合）

**Non-Goals:**

- 批量补记 API
-  cron/自动违约判定
- 登记后修改删除
- REMEDY / EXEMPT / SIGN 写入账本
- 推送、完整今日首页 SCREEN

## Decisions

### 1. 读写分离：虚拟状态 + append-only 账本

```
READ:  枚举 [start_date .. end_date] 每个 sleep_day
         LEFT JOIN sleep_ledger_event
         → 有记录: FULFILLED | BREACH
         → 无记录且 day ≤ today: PENDING（待登记）
         → 无记录且 day > today:  UPCOMING（未到期）

WRITE: POST 一条 { recordDate, eventType } → INSERT only
```

**备选**：签约时 bulk INSERT PENDING 行 → 拒绝，因改契约日期需同步维护且违反 append-only 写入语义。

### 2. 表结构

```sql
-- V4__init_sleep_ledger.sql

CREATE TABLE sleep_ledger_event (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    contract_id BIGINT NOT NULL,
    record_date DATE NOT NULL COMMENT '睡眠日',
    event_type VARCHAR(16) NOT NULL COMMENT 'FULFILLED|BREACH',
    note TEXT DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_contract_record_date (contract_id, record_date),
    KEY idx_user_id (user_id),
    KEY idx_contract_id (contract_id),
    KEY idx_record_date (record_date),
    CONSTRAINT fk_ledger_user FOREIGN KEY (user_id) REFERENCES user_account(id),
    CONSTRAINT fk_ledger_contract FOREIGN KEY (contract_id) REFERENCES contract(id)
) COMMENT='睡眠账本事件表';
```

- `event_type` MVP 仅 `FULFILLED` / `BREACH`
- `UNIQUE(contract_id, record_date)` 保证一日一条

### 3. 登记校验规则

| 规则 | 行为 |
|------|------|
| 用户有 ACTIVE 睡眠契约 | 必须，否则 404 |
| `start_date ≤ record_date ≤ end_date` | 否则 400 |
| `record_date ≤ today`（服务器 Asia/Shanghai 或客户端传 date 字符串校验） | 否则 400（禁止未来登记） |
| 同日无已有记录 | 否则 409 DUPLICATE_RECORD |
| event_type | 必须 FULFILLED 或 BREACH |

### 4. API 设计

```
POST /api/ledger/sleep/records
  Body: { "recordDate": "2026-06-28", "eventType": "FULFILLED", "note": null }
  → 201/200 + event DTO

GET /api/ledger/sleep/daily-view
  Query: from?, to?（默认契约全范围；可限最近 N 天）
  → data: { contractId, summary, days: [{ recordDate, status, eventType?, createdAt? }] }
     status: PENDING | FULFILLED | BREACH | UPCOMING

GET /api/ledger/sleep/summary
  → { obligationDays, recordedDays, pendingDays, fulfilledDays, breachDays }
     obligationDays = days from start_date to min(end_date, today)
```

所有接口需 JWT；`user_id` 从 token 解析，且 `contract_id` 必须属于该用户。

### 5. 日视图 status 计算（服务端）

对契约 `[start_date, end_date]` 内每一天 `d`：

```
if d > today        → UPCOMING
else if ledger row  → FULFILLED | BREACH（来自 event_type）
else                → PENDING
```

### 6. 前端页面

- 新页面 `pages/ledger/index`（或 `pages/sleep-ledger`）MVP
- 顶部 summary（义务/待登记/履约/违约）
- `LedgerTimeline` + `LedgerItem` 列表
- 待登记行：可点击 → 确认弹窗 → 选履约/违约 → `POST` 单条
- 未到期行：灰色，不可点
- 已登记行：展示 tag，不可改

`CreditEventType` 扩展读模型常量（不入库）：

- `PENDING` → 待登记
- `UPCOMING` → 未到期

### 7. 测试策略

- 后端：`SleepLedgerService` 单元测试（登记、重复、日期边界、日视图计算、summary）
- 后端：`LedgerController` `@WebMvcTest`
- 前端：`utils/ledgerView.ts` 纯函数（status 映射、summary 辅助）单元测试

## Risks / Trade-offs

| 风险 | 缓解 |
|------|------|
| 待登记不等于违约，义务感靠 UI 强调 | 列表突出待登记数量；summary 展示 pendingDays |
| 用户长期不登记堆积待登记 | MVP 无推送；列表按日期倒序待登记优先 |
| 改契约 end_date 影响义务天数 | summary 按当前契约实时计算；历史 ledger 不回溯 |
| 时区导致「今天」边界 | 统一用 DATE 字符串 + 服务端 Asia/Shanghai 校验 |

## Migration Plan

1. 部署 Flyway V4（无数据迁移，纯新表）
2. 部署后端 API
3. 发布小程序账本页
4. 回滚：小程序隐藏入口；后端 API 可保留，表可暂不删

## Open Questions

（均已确认，无遗留）

- 批量补记：MVP 不做
- 未到期 vs 待登记：区分展示
- 一次一条登记：是
