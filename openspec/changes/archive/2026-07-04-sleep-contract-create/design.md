## Context

`pages/contract-create` 当前为占位页。设计系统已有 `TimePickerField`、`FormSection`、`ContractCard`，但缺少日期区间选择、违约条款 UI 与契约文书组件。后端仅有 `user_account` / `user_wechat_bind`，无契约相关表。

用户确认决策：
1. **仅 `target_bedtime`**，不设 `latest_allowed`
2. **生效期**：前端 `DateRangeField`，用户自选起止日期，粒度为天
3. **表单 ↔ 文书双向绑定**，点击「确认签约」时**统一调 API**（无服务端 draft）
4. **一人一契约，可修改**（非多合约并行）

## Goals / Non-Goals

**Goals:**

- V3 Flyway：`contract` + `sleep_contract` + `contract_breach_clause`
- 后端 upsert：每用户最多一份 SLEEP 契约，支持创建与更新
- SCREEN 01 完整表单 + 实时文书预览 + 确认签约
- 违约条款：记录（必选）、复盘（可选）、自定义（可选+可编辑）

**Non-Goals:**

- 多 ACTIVE 合约、监督人邀请、服务端草稿
- 履约判定、今日首页、ledger 写入（后续 change）
- `latest_allowed` 字段

## Decisions

### 1. 表结构：主表 + 扩展 + 条款实例

```
contract (通用)
  ├── sleep_contract (1:1, target_bedtime)
  └── contract_breach_clause (1:N, 条款快照)
```

- `contract.type = 'SLEEP'`（MVP 固定，预留扩展）
- `UNIQUE (user_id, type)` 保证一人一睡眠契约
- 更新时替换 `contract_breach_clause` 行（事务内 delete + insert）

### 2. 时间字段

- `sleep_contract.target_bedtime`：`TIME`，存 HH:mm
- `contract.start_date` / `end_date`：`DATE`，粒度天
- 前端展示「共 N 天」由 `endDate - startDate + 1` 计算，不入库

### 3. 违约条款模型

| clause_type | enabled | editable | MVP 默认 |
|-------------|---------|----------|----------|
| RECORD | 强制 true | false | 永久写入信用账本 |
| REVIEW | 用户选 | false | 次日 3 题复盘 |
| CUSTOM | 用户选 | true (content_text) | 用户输入 |

「第三条 · 不可撤销」为文书固定模板文本，非 DB 条款行。

### 4. API 契约

```
GET  /api/contracts/sleep/mine     → 当前用户睡眠契约（无则 404 或 data null）
PUT  /api/contracts/sleep          → 创建或更新（upsert，需 JWT）
```

请求体：

```json
{
  "targetBedtime": "23:00",
  "startDate": "2026-06-24",
  "endDate": "2026-07-23",
  "breachClauses": [
    { "type": "RECORD", "enabled": true },
    { "type": "REVIEW", "enabled": true },
    { "type": "CUSTOM", "enabled": true, "contentText": "连续 3 次..." }
  ]
}
```

响应 data 含 `contractNo`、`signedAt`、完整契约 DTO。

- 首次 PUT → INSERT，`contract_no` 生成 `C-{yyyyMMdd}-{seq}`
- 再次 PUT → UPDATE 主表/扩展表/条款，**编号不变**，`signed_at` 更新为当前时间

校验：`startDate <= endDate`；`targetBedtime` 非空；RECORD 必须 enabled。

### 5. 前端交互

```
contract-create 页面 state (SleepContractForm)
        │
        ├─▶ FormSection 01: TimePickerField
        ├─▶ FormSection 02: DateRangeField
        ├─▶ FormSection 03: BreachClauseSelector
        │
        └─▶ SleepContractDocument (mode=preview, 双向绑定)
                │
        [确认签约] ──▶ services/contract.ts upsertSleepContract()
```

- 页面 `onLoad`：`getMySleepContract()`，有数据则回填表单（编辑模式）
- 文书组件纯展示 + 从 props 渲染，无独立 state

### 6. 组件划分

| 组件 | 职责 |
|------|------|
| `DateRangeField` | 起止日期选择，天粒度，显示共 N 天 |
| `BreachClauseSelector` | 三条违约条款 UI（锁/勾选/textarea） |
| `SleepContractDocument` | 文书预览/已签（标题、编号、三条正文、甲乙方、印章） |

### 7. 测试策略

- 后端：`SleepContractService` 单元测试（create、update、validation、one-per-user）
- 后端：`ContractController` `@WebMvcTest`
- 前端：`utils/contractForm.ts` 纯函数（天数计算、条款校验、DTO 映射）单元测试

## Flyway V3 SQL（设计稿）

```sql
-- V3__init_sleep_contract.sql

CREATE TABLE contract (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '契约ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    type VARCHAR(32) NOT NULL DEFAULT 'SLEEP' COMMENT '契约类型',
    contract_no VARCHAR(32) NOT NULL COMMENT '契约编号',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1=ACTIVE',
    start_date DATE NOT NULL COMMENT '生效开始日期',
    end_date DATE NOT NULL COMMENT '生效结束日期',
    signed_at DATETIME DEFAULT NULL COMMENT '最近签约时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_type (user_id, type),
    UNIQUE KEY uk_contract_no (contract_no),
    KEY idx_user_id (user_id),
    CONSTRAINT fk_contract_user FOREIGN KEY (user_id) REFERENCES user_account(id)
) COMMENT='契约主表';

CREATE TABLE sleep_contract (
    contract_id BIGINT PRIMARY KEY COMMENT '契约ID',
    target_bedtime TIME NOT NULL COMMENT '目标入眠时间',
    CONSTRAINT fk_sleep_contract FOREIGN KEY (contract_id) REFERENCES contract(id)
) COMMENT='睡眠契约扩展表';

CREATE TABLE contract_breach_clause (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '条款ID',
    contract_id BIGINT NOT NULL COMMENT '契约ID',
    clause_type VARCHAR(16) NOT NULL COMMENT 'RECORD/REVIEW/CUSTOM',
    enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
    content_text TEXT DEFAULT NULL COMMENT '自定义条款内容',
    sort_order TINYINT NOT NULL DEFAULT 0 COMMENT '排序',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_contract_id (contract_id),
    CONSTRAINT fk_breach_clause_contract FOREIGN KEY (contract_id) REFERENCES contract(id)
) COMMENT='契约违约条款实例表';
```

## Risks / Trade-offs

| 风险 | 缓解 |
|------|------|
| 签约后修改条款影响已发生履约事件 | MVP 允许改；后续 change 限制 ACTIVE 且已有 ledger 时不可改条款 |
| 前端未提交丢数据 | MVP 可接受；后续可选 localStorage 缓存 |
| upsert 与 UNIQUE 冲突 | Service 层先查 user_id+type，有则 update 无则 insert |

## Open Questions

（已全部确认，无遗留）
