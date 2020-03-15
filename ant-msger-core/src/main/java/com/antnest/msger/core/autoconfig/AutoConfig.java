package com.antnest.msger.core.autoconfig;

import com.antnest.msger.core.constant.GlobalConfig;
import com.antnest.msger.core.endpoint.BaseIMEndpoint;
import com.antnest.msger.core.endpoint.BaseJt808Endpoint;
import com.antnest.msger.core.endpoint.IMEndpoint;
import com.antnest.msger.core.endpoint.JT808Endpoint;
import com.antnest.msger.core.mapping.HandlerMapper;
import com.antnest.msger.core.mapping.SpringHandlerMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;

import java.util.Map;

@Configuration
public class AutoConfig {

    @Value("#{${system.msger.protocol}}")
    Map<String, Integer> protocolMap;

    @Bean(name = "JT808Endpoint")
    @ConditionalOnMissingBean(BaseJt808Endpoint.class)
    public BaseJt808Endpoint baseJt808Endpoint() {
        return new JT808Endpoint();
    }

    @Bean(name = "IMEndpoint")
    @ConditionalOnMissingBean(BaseIMEndpoint.class)
    public BaseIMEndpoint baseIMEndpoint() {
        return new IMEndpoint();
    }

    /**
     * 把protocolBusiness缓存起来
     */
    @Bean(name = "globalConfig")
    public GlobalConfig globalConfig() {
        // 缓存业务协议配置表
        return new GlobalConfig(protocolMap);
    }

    @Bean
    @DependsOn({"globalConfig","JT808Endpoint","IMEndpoint"})
    public HandlerMapper handlerMapper() {
        return new SpringHandlerMapper("com.antnest.msger.core.endpoint");
    }
}