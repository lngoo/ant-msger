package com.antnest.msger.converter.autoconfig;

import com.antnest.msger.converter.constant.GlobalConfig;
import com.antnest.msger.converter.mapping.HandlerMapper;
import com.antnest.msger.converter.mapping.SpringHandlerMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.Map;

@Configuration
public class AutoConfig {

    @Value("#{${system.msger.protocol}}")
    Map<String, Integer> protocolMap;

    /**
     * 把protocolBusiness缓存起来
     */
    @Bean(name = "globalConfig")
    public GlobalConfig globalConfig() {
        // 缓存业务协议配置表
        return new GlobalConfig(protocolMap);
    }

    @Bean
    @DependsOn("globalConfig")
    public HandlerMapper handlerMapper() {
        return new SpringHandlerMapper("com.antnest.msger.converter.endpoint");
    }
}