package com.ant.msger.main.web.config;

import com.ant.msger.main.framework.mapping.HandlerMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ant.msger.main.framework.UDPServer;
import com.ant.msger.main.framework.spring.SpringHandlerMapper;

@Configuration
public class NettyConfig {

    @Value("${system.session.minutes:5}")
    private int sessionMinutes;

    @Bean
    public UDPServer udpServer() {
//        TCPServer server = new TCPServer(7611, (byte) 0x7e, handlerMapper());
        UDPServer server = new UDPServer(7612, (byte) 0x7e, handlerMapper(), sessionMinutes);
        server.startServer();
        return server;
    }

    @Bean
    public HandlerMapper handlerMapper() {
        return new SpringHandlerMapper("com.ant.msger.main.web.endpoint");
    }
}