package com.ant.msger.main.web.config;

import com.ant.msger.main.framework.TCPServer;
import com.ant.msger.main.framework.WebsocketServer;
import com.ant.msger.main.framework.commons.constant.GlobalConfig;
import com.ant.msger.main.framework.mapping.HandlerMapper;
import com.ant.msger.main.framework.redis.RedisFragMsgService;
import com.ant.msger.main.framework.session.TopicManager;
import com.ant.msger.main.persistence.dao.TopicUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ant.msger.main.framework.UDPServer;
import com.ant.msger.main.framework.spring.SpringHandlerMapper;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.SortParameters;

import java.util.Map;

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

    @Autowired
    private TopicUserMapper topicUserMapper;

    @Autowired
    private RedisFragMsgService redisFragMsgService;

    @Value("#{${system.msger.protocol}}")
    Map<String, Integer> protocolMap;

    @Bean
    @ConditionalOnExpression("${system.udp.enable:true}")
    public UDPServer udpServer() {
        UDPServer server = new UDPServer(udpPort, handlerMapper(),redisFragMsgService, sessionMinutes);
        server.startServer();
        return server;
    }

    @Bean
    @ConditionalOnExpression("${system.tcp.enable:false}")
    public TCPServer tcpServer() {
        TCPServer server = new TCPServer(tcpPort, handlerMapper(), redisFragMsgService, sessionMinutes);
        server.startServer();
        return server;
    }

    @Bean
    @ConditionalOnExpression("${system.websocket.enable:false}")
    public WebsocketServer websocketServer() {
        WebsocketServer server = new WebsocketServer(websocketPort, handlerMapper(), redisFragMsgService, sessionMinutes);
        server.startServer();
        return server;
    }

    /**
     * 把protocolBusiness缓存起来
     */
    @Bean(name="globalConfig")
    public GlobalConfig globalConfig() {
        // 初始化topicuser缓存数据
        TopicManager.getInstance().loadDBDatas(topicUserMapper);
        // 缓存业务协议配置表
        return new GlobalConfig(protocolMap);
    }

    @Bean
    @DependsOn("globalConfig")
    public HandlerMapper handlerMapper() {
        return new SpringHandlerMapper("com.ant.msger.main.web.endpoint");
    }
}