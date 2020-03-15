package com.antnest.msger.main.web.config;

import com.alibaba.fastjson.JSONObject;
import com.antnest.msger.main.framework.TCPServer;
import com.antnest.msger.main.framework.WebsocketServer;
import com.antnest.msger.core.constant.GlobalConfig;
import com.antnest.msger.core.mapping.HandlerMapper;
import com.antnest.msger.core.redis.RedisFragMsgService;
import com.antnest.msger.main.framework.session.TopicManager;
import com.antnest.msger.main.persistence.dao.TopicMapper;
import com.antnest.msger.main.persistence.dao.TopicUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.antnest.msger.main.framework.UDPServer;
import com.antnest.msger.core.mapping.SpringHandlerMapper;
import org.springframework.context.annotation.DependsOn;

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
    private TopicMapper topicMapper;

    @Autowired
    private HandlerMapper handlerMapper;

    @Autowired
    private RedisFragMsgService redisFragMsgService;

//    @Value("#{${system.msger.protocol}}")
//    Map<String, Integer> protocolMap;

    @Bean
    @ConditionalOnExpression("${system.udp.enable:true}")
    public UDPServer udpServer() {
        UDPServer server = new UDPServer(udpPort, handlerMapper,redisFragMsgService, sessionMinutes);
        server.startServer();
        return server;
    }

    @Bean
    @ConditionalOnExpression("${system.tcp.enable:false}")
    public TCPServer tcpServer() {
        TCPServer server = new TCPServer(tcpPort, handlerMapper, redisFragMsgService, sessionMinutes);
        server.startServer();
        return server;
    }

    @Bean
    @ConditionalOnExpression("${system.websocket.enable:false}")
    public WebsocketServer websocketServer() {
        WebsocketServer server = new WebsocketServer(websocketPort, handlerMapper, redisFragMsgService, sessionMinutes);
        server.startServer();
        return server;
    }

    /**
     */
    @Bean
    public JSONObject loadData() {
        // 初始化topicuser缓存数据
        TopicManager.getInstance().loadDBDatas(topicMapper, topicUserMapper);
        return new JSONObject();
    }
//
//    @Bean
//    @DependsOn("globalConfig")
//    public HandlerMapper handlerMapper() {
//        return new SpringHandlerMapper("com.antnest.msger.core.endpoint");
//    }
}