package com.antnest.msger.main.web.config;

import com.antnest.msger.core.constant.GlobalConfig;
import com.antnest.msger.core.endpoint.BaseIMEndpoint;
import com.antnest.msger.core.endpoint.BaseJt808Endpoint;
import com.antnest.msger.core.endpoint.IMEndpoint;
import com.antnest.msger.core.endpoint.JT808Endpoint;
import com.antnest.msger.core.mapping.HandlerMapper;
import com.antnest.msger.core.mapping.SpringHandlerMapper;
import com.antnest.msger.main.web.endpoint.CompleteIMEndpoint;
import com.antnest.msger.main.web.endpoint.CompleteJT808Endpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.Map;

@Configuration
public class MsgerAutoConfig {

    @Bean(name = "JT808Endpoint")
    public BaseJt808Endpoint baseJt808Endpoint() {
        return new CompleteJT808Endpoint();
    }

    @Bean(name = "IMEndpoint")
    public BaseIMEndpoint baseIMEndpoint() {
        return new CompleteIMEndpoint();
    }
}