package com.ant.msger.main.mq.subscriber;

import com.ant.msger.base.common.MessageId;
import com.ant.msger.base.dto.jt808.CommonResult;
import com.ant.msger.base.dto.jt808.basics.Message;
import com.ant.msger.base.message.AntSendChannelMsg;
import com.ant.msger.main.framework.commons.enumeration.ProtocolCommunication;
import com.ant.msger.main.framework.sender.ProtocolMsgSender;
import com.ant.msger.main.framework.session.Session;
import com.ant.msger.main.framework.session.SessionManager;
import com.thoughtworks.xstream.XStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SendDataPuller {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Value("${redis.key.queue.response}")
    String redisKey;

    private XStream xstream = new XStream();
    private ProtocolMsgSender protocolMsgSender = new ProtocolMsgSender();

    public void doJob() {

        if (StringUtils.isEmpty(redisKey)) {
            System.out.println("### [send] no response redis key config. stop all...");
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
                        System.out.println("###[send] no response data. sleep 3 seconds." + System.currentTimeMillis());
                    } else {
                        System.out.println("###[send] got one data." + System.currentTimeMillis());
                        AntSendChannelMsg antSendChannelMsg = (AntSendChannelMsg) xstream.fromXML(data);
                        switch (antSendChannelMsg.getSendType()) {
                            case TO_SESSION:
                                sendMsgToDevice(antSendChannelMsg.getMessage());
                                break;
                                // todo 还没处理IM消息
                            case TO_USER:
                                break;
                            case TO_TOPIC:

                        }
                    }
                }
            }
        }).start();

    }

    private void sendMsgToDevice(Message message) {
        Session session = SessionManager.getInstance().getByMobileNumber(message.getMobileNumber());
        if (message.getBody() instanceof CommonResult) {
            CommonResult commonResult = (CommonResult) message.getBody();
            if (commonResult.getReplyId() == MessageId.终端鉴权
                    && commonResult.getResultCode() == CommonResult.Success) {
                // 终端session鉴权成功，更新下链接时间
                session.setLastCommunicateTimeStamp(System.currentTimeMillis());
            }
        }

        message.setSerialNumber(session.currentFlowId());

        // 发送消息
        if (ProtocolCommunication.UDP == session.getProtocolCommunication()) {
            protocolMsgSender.sendUdpMsgSingle(session, message);
        } else if (ProtocolCommunication.WEBSOCKET == session.getProtocolCommunication()) {
            protocolMsgSender.sendWebsocketMsgSingle(session, message);
        } else {
            protocolMsgSender.sendTcpMsgSingle(session, message);
        }
    }
}
