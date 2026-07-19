ALTER TABLE user_account
    MODIFY COLUMN phone VARCHAR(20) NULL COMMENT '手机号（可空，微信静默登录用户暂无）';
