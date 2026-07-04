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
