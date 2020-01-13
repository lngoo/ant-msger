package com.ant.msger.main.web.config;

import com.ant.msger.main.framework.mapping.HandlerMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ant.msger.main.framework.UDPServer;
import com.ant.msger.main.framework.spring.SpringHandlerMapper;

@Configuration
public class NettyConfig {

    @Value("${system.session.minutes:5}")
    private int sessionMinutes;

    @Value("${system.udp.port:7612}")
    private int udpPort;

    @Value("${system.tcp.port:7613}")
    private int tcpPort;

    @Bean
    @ConditionalOnExpression("${system.udp.enable:true}")
    public UDPServer udpServer() {
        UDPServer server = new UDPServer(udpPort, (byte) 0x7e, handlerMapper(), sessionMinutes);
        server.startServer();
        return server;
    }

    @Bean
    @ConditionalOnExpression("${system.tcp.enable:false}")
    public UDPServer tcpServer() {
        UDPServer server = new UDPServer(udpPort, (byte) 0x7e, handlerMapper(), sessionMinutes);
        server.startServer();
        return server;
    }

    @Bean
    public HandlerMapper handlerMapper() {
        return new SpringHandlerMapper("com.ant.msger.main.web.endpoint");
    }
}