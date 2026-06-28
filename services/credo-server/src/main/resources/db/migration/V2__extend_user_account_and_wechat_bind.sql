ALTER TABLE user_account
    ADD COLUMN country_code VARCHAR(8) NOT NULL DEFAULT '86' COMMENT '国家区号' AFTER phone,
    ADD COLUMN last_login_at DATETIME DEFAULT NULL COMMENT '最后登录时间';

CREATE TABLE user_wechat_bind (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '绑定ID',

    user_id BIGINT NOT NULL COMMENT '用户ID',

    app_id VARCHAR(32) NOT NULL COMMENT '小程序 AppID',

    open_id VARCHAR(64) NOT NULL COMMENT '微信 openid',

    union_id VARCHAR(64) DEFAULT NULL COMMENT '微信 unionid',

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_open_id (open_id),
    KEY idx_user_id (user_id),
    KEY idx_union_id (union_id),
    CONSTRAINT fk_wechat_bind_user FOREIGN KEY (user_id) REFERENCES user_account(id)
) COMMENT='用户微信绑定表';
