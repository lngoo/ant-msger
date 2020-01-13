package com.ant.msger.main.mq.puller;

import com.ant.msger.base.dto.jt808.basics.Message;
import com.ant.msger.main.framework.session.Session;
import com.ant.msger.main.framework.session.SessionManager;
import com.thoughtworks.xstream.XStream;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ResponseDataPuller {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Value("${redis.key.queue.response}")
    String redisKey;

    private XStream xstream = new XStream();

    public void doJob() {

        if (StringUtils.isEmpty(redisKey)) {
            System.out.println("### no response redis key config. stop all...");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String data = stringRedisTemplate.opsForList().rightPop(redisKey);
                        if (null == data) {
                            // Sleep 3秒
                            Thread.sleep(3000);
                            System.out.println("### no response data. sleep 3 seconds." + System.currentTimeMillis());
                        } else {
                            System.out.println("### got one data." + System.currentTimeMillis());
//                            Message message = JsonUtils.toObj(Message.class, data);
//                            Session session = SessionManager.getInstance().getByMobileNumber(message.getMobileNumber());
//                            message.setSerialNumber(session.currentFlowId());
                            Message message = (Message) xstream.fromXML(data);
                            Session session = SessionManager.getInstance().getByMobileNumber(message.getMobileNumber());
                            message.setSerialNumber(session.currentFlowId());

                            // 发送消息
                            Channel channel = session.getChannel();
                            channel.connect(session.getSocketAddress());
                            ChannelFuture future = channel.writeAndFlush(message).sync();
                            channel.disconnect();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}