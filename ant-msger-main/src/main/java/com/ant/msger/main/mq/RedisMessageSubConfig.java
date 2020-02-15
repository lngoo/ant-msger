package com.ant.msger.main.mq;

import com.ant.msger.main.mq.subscriber.ResponseMsgSubscriber;
import com.ant.msger.main.mq.subscriber.TaskMsgSubscriber;
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

    @Value("${redis.key.queue.task}")
    private String redisKeyTaskChannel;

    @Value("${system.msger.id}")
    private String msgerId;

    /**
     * 创建连接工厂
     * @param connectionFactory
     * @return
     */
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter responseListenerAdapter,
                                                   MessageListenerAdapter taskListenerAdapter){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        //接受消息的key
        container.addMessageListener(responseListenerAdapter, new ChannelTopic(redisKeyResponseChannel.concat(":").concat(msgerId)));
        container.addMessageListener(taskListenerAdapter, new ChannelTopic(redisKeyTaskChannel));

        return container;
    }


    /**
     * 绑定消息监听者和接收监听的方法
     * @return
     */
    @Bean
    public MessageListenerAdapter responseListenerAdapter(ResponseMsgSubscriber subscriber){
        return new MessageListenerAdapter(subscriber);
    }

    /**
     * 绑定消息监听者和接收监听的方法
     * @return
     */
    @Bean
    public MessageListenerAdapter taskListenerAdapter(TaskMsgSubscriber subscriber){
        return new MessageListenerAdapter(subscriber);
    }

    /**
     * 注册 响应通道 订阅者
     * @return
     */
    @Bean
    public ResponseMsgSubscriber responseReceiver() {
        return new ResponseMsgSubscriber();
    }

    /**
     * 注册 任务通道 订阅者
     * @return
     */
    @Bean
    public TaskMsgSubscriber taskReceiver() {
        return new TaskMsgSubscriber();
    }
}
