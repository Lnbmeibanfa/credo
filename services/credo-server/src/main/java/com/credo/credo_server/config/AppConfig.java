package com.credo.credo_server.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.credo.credo_server.mapper")
@EnableConfigurationProperties({WeChatProperties.class, JwtProperties.class})
public class AppConfig {
}
