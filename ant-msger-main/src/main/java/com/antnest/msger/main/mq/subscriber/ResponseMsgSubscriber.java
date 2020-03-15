package com.antnest.msger.main.mq.subscriber;

import com.antnest.msger.core.common.MessageId;
import com.antnest.msger.core.dto.jt808.CommonResult;
import com.antnest.msger.core.message.MessageExternal;
import com.antnest.msger.core.constant.GlobalConfig;
import com.antnest.msger.main.framework.commons.enumeration.ProtocolCommunication;
import com.antnest.msger.main.framework.sender.ProtocolMsgSender;
import com.antnest.msger.main.framework.session.Session;
import com.antnest.msger.main.framework.session.SessionManager;
import com.antnest.msger.main.mq.util.LocalProtoBufUtil;
import com.antnest.msger.main.mq.util.MessageConvertUtil;
import com.antnest.msger.proto.ProtoMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * 响应通道（to-persistence）数据处理器
 */
public class ResponseMsgSubscriber implements MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger("responseChannel");

    private ProtocolMsgSender protocolMsgSender = new ProtocolMsgSender();

    @Override
    public void onMessage(Message message, byte[] bytes) {
        LOG.info("### receive one data. msg={}", message.getBody());

        byte[] body = message.getBody();
        try {
            ProtoMain.Message message1 = ProtoMain.Message.parseFrom(body);
            MessageExternal external = LocalProtoBufUtil.copyProtoBeanToMessageExternal(message1);
            com.antnest.msger.core.dto.jt808.basics.Message message2 = MessageConvertUtil.toInternal(external, GlobalConfig.protocolBusinessMap());

            // 发送到设备
            String sessionId = message1.getSendTo();
            sendMsgToSession(message2, sessionId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("#################### end ... ");
    }

    private void sendMsgToSession(com.antnest.msger.core.dto.jt808.basics.Message message, String sessionId) {
        Session session = SessionManager.getInstance().getBySessionId(sessionId);
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
