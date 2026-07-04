CREATE TABLE sleep_ledger_event (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '账本事件ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    contract_id BIGINT NOT NULL COMMENT '契约ID',
    record_date DATE NOT NULL COMMENT '睡眠日',
    event_type VARCHAR(16) NOT NULL COMMENT 'FULFILLED|BREACH',
    note TEXT DEFAULT NULL COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登记时间',
    UNIQUE KEY uk_contract_record_date (contract_id, record_date),
    KEY idx_user_id (user_id),
    KEY idx_contract_id (contract_id),
    KEY idx_record_date (record_date),
    CONSTRAINT fk_ledger_user FOREIGN KEY (user_id) REFERENCES user_account(id),
    CONSTRAINT fk_ledger_contract FOREIGN KEY (contract_id) REFERENCES contract(id)
) COMMENT='睡眠账本事件表';
