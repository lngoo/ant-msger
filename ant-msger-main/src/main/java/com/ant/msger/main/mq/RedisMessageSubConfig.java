package com.ant.msger.main.mq;

import com.ant.msger.main.mq.subscriber.ResponseMsgSubscriber;
import com.ant.msger.main.mq.subscriber.SendToTopicMsgSubscriber;
import com.ant.msger.main.mq.subscriber.SendToUserMsgSubscriber;
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

    @Value("${redis.key.queue.send.user}")
    private String redisKeySendToUserChannel;

    @Value("${redis.key.queue.send.topic}")
    private String redisKeySendToTopicChannel;

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
                                                   MessageListenerAdapter sendToUserListenerAdapter,
                                                   MessageListenerAdapter sendToTopicListenerAdapter,
                                                   MessageListenerAdapter taskListenerAdapter){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        //接受消息的key
        container.addMessageListener(responseListenerAdapter, new ChannelTopic(redisKeyResponseChannel.concat(":").concat(msgerId)));
        container.addMessageListener(taskListenerAdapter, new ChannelTopic(redisKeyTaskChannel));
        container.addMessageListener(sendToUserListenerAdapter, new ChannelTopic(redisKeySendToUserChannel));
        container.addMessageListener(sendToTopicListenerAdapter, new ChannelTopic(redisKeySendToTopicChannel));

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

    @Bean
    public MessageListenerAdapter sendToUserListenerAdapter(SendToUserMsgSubscriber subscriber){
        return new MessageListenerAdapter(subscriber);
    }
    @Bean
    public MessageListenerAdapter sendToTopicListenerAdapter(SendToTopicMsgSubscriber subscriber){
        return new MessageListenerAdapter(subscriber);
    }

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
     * 注册 发送到用户 订阅者
     * @return
     */
    @Bean
    public SendToUserMsgSubscriber sendToUserReceiver() {
        return new SendToUserMsgSubscriber();
    }

    /**
     * 注册 发送到用户 订阅者
     * @return
     */
    @Bean
    public SendToTopicMsgSubscriber sendToTopicReceiver() {
        return new SendToTopicMsgSubscriber();
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
