package com.ant.msger.main.mq.puller;

import com.ant.msger.base.common.MessageId;
import com.ant.msger.base.dto.jt808.Authentication;
import com.ant.msger.base.dto.jt808.CommonResult;
import com.ant.msger.base.dto.jt808.basics.Message;
import com.ant.msger.main.framework.handler.Protocol;
import com.ant.msger.main.framework.session.Session;
import com.ant.msger.main.framework.session.SessionManager;
import com.thoughtworks.xstream.XStream;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

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
                while (true) {
                    String data = stringRedisTemplate.opsForList().rightPop(redisKey);
                    if (null == data) {
                        // Sleep 3秒
                        try {
                            Thread.sleep(3000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println("### no response data. sleep 3 seconds." + System.currentTimeMillis());
                    } else {
                        try {
                            System.out.println("### got one data." + System.currentTimeMillis());
                            Message message = (Message) xstream.fromXML(data);

                            if (message.getBody() instanceof CommonResult) {
                                CommonResult commonResult = (CommonResult) message.getBody();
                                if (commonResult.getReplyId() == MessageId.终端鉴权
                                    && commonResult.getResultCode() == CommonResult.Success) {
                                    // TODO 终端session已授权
                                }
                            }

                            Session session = SessionManager.getInstance().getByMobileNumber(message.getMobileNumber());
                            message.setSerialNumber(session.currentFlowId());

                            // 发送消息
                            Channel channel = session.getChannel();
                            if (Protocol.UDP == session.getProtocol()) {
                                channel.connect(session.getSocketAddress());
                                ChannelFuture future = channel.writeAndFlush(message).sync();
                                channel.disconnect();
                            } else if (Protocol.WEBSOCKET == session.getProtocol()) {
                                // TODO websocket 返回数据给客户端
//                                TextWebSocketFrame tws = new TextWebSocketFrame(message);
//                                channel.writeAndFlush(tws).sync();
                            } else {
                                ChannelFuture future = channel.writeAndFlush(message).sync();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();

    }
}
