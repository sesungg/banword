package com.banword.config;

import com.banword.service.BanwordValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingBean(BanwordValidator.class) // 사용자가 직접 빈을 등록하지 않은 경우만 자동 등록
@ComponentScan("com.banword")
public class BanwordAutoConfiguration {
}
