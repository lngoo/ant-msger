package com.ant.msger.main.mq;

import com.ant.msger.main.mq.subscriber.ResponseMsgSubscriber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * redis数据监听配置类
 */
@Configuration
public class RedisMessageSubConfig {

    @Value("${redis.key.queue.response}")
    private String redisKeyResponseChannel;

    @Value("${system.msger.id}")
    private String msgerId;

    /**
     * 创建连接工厂
     * @param connectionFactory
     * @param listenerAdapter
     * @return
     */
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter listenerAdapter){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        //接受消息的key
        container.addMessageListener(listenerAdapter, new ChannelTopic(redisKeyResponseChannel.concat(":").concat(msgerId)));
//        container.addMessageListener(listenerAdapter,new PatternTopic("phone"));

        return container;
    }


    /**
     * 绑定消息监听者和接收监听的方法
     * @return
     */
    @Bean
    public MessageListenerAdapter listenerAdapter(ResponseMsgSubscriber subscriber){
        return new MessageListenerAdapter(subscriber);
    }

    /**
     * 注册发送-session订阅者
     * @return
     */
    @Bean
    public ResponseMsgSubscriber receiver() {
        return new ResponseMsgSubscriber();
    }
}
