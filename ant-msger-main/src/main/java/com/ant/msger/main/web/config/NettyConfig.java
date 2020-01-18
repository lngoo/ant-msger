package com.ant.msger.main.web.config;

import com.ant.msger.main.framework.TCPServer;
import com.ant.msger.main.framework.WebsocketServer;
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

    @Value("${system.websocket.port:7614}")
    private int websocketPort;

    @Bean
    @ConditionalOnExpression("${system.udp.enable:true}")
    public UDPServer udpServer() {
        UDPServer server = new UDPServer(udpPort, (byte) 0x7e, handlerMapper(), sessionMinutes);
        server.startServer();
        return server;
    }

    @Bean
    @ConditionalOnExpression("${system.tcp.enable:false}")
    public TCPServer tcpServer() {
        TCPServer server = new TCPServer(tcpPort, (byte) 0x7e, handlerMapper());
        server.startServer();
        return server;
    }

    @Bean
    @ConditionalOnExpression("${system.websocket.enable:false}")
    public WebsocketServer websocketServer() {
        WebsocketServer server = new WebsocketServer(websocketPort, (byte) 0x7e, handlerMapper());
        server.startServer();
        return server;
    }

    @Bean
    public HandlerMapper handlerMapper() {
        return new SpringHandlerMapper("com.ant.msger.main.web.endpoint");
    }
}