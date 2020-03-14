package com.ant.msger.main.web.endpoint;

import com.ant.msger.base.annotation.Endpoint;
import com.ant.msger.base.annotation.Mapping;
import com.ant.msger.base.dto.jt808.IMMsg;
import com.ant.msger.base.dto.jt808.*;
import com.ant.msger.base.dto.jt808.basics.Message;
import com.ant.msger.base.message.SyncFuture;
import com.ant.msger.main.framework.commons.constant.GlobalConfig;
import com.ant.msger.main.framework.commons.enumeration.ProtocolBusiness;
import com.ant.msger.main.framework.session.MessageManager;
import com.ant.msger.main.framework.session.Session;
import com.ant.msger.main.framework.session.SessionManager;
import com.ant.msger.main.mq.publisher.RequestDataPublisher;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.ant.msger.base.common.MessageId.*;


@Endpoint
@Component
public class IMEndpoint extends BaseEndpoint {

    @Autowired
    RequestDataPublisher jt808Sender;

    private static final Logger logger = LoggerFactory.getLogger(IMEndpoint.class.getSimpleName());

    private SessionManager sessionManager = SessionManager.getInstance();

    private MessageManager messageManager = MessageManager.INSTANCE;

    @Override
    public Integer getPointType() {
        return GlobalConfig.protocolBusinessMap().get(ProtocolBusiness.AntIM.name());
    }

    //TODO Test
    public Object send(String mobileNumber, String hexMessage) {

        if (!hexMessage.startsWith("7e"))
            hexMessage = "7e" + hexMessage + "7e";
        ByteBuf msg = Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(hexMessage));
        Session session = SessionManager.getInstance().getByUserAlias(mobileNumber).iterator().next();


        session.getChannel().writeAndFlush(msg);

        String key = mobileNumber;
        SyncFuture receive = messageManager.receive(key);
        try {
            return receive.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            messageManager.remove(key);
            e.printStackTrace();
        }
        return null;
    }

    public Object send(Message message) {
        return send(message, true);
    }

    public Object send(Message message, boolean hasReplyFlowIdId) {
        String mobileNumber = message.getMobileNumber();
        Session session = sessionManager.getByUserAlias(mobileNumber).iterator().next();
        message.setSerialNumber(session.currentFlowId());

        session.getChannel().writeAndFlush(message);

        String key = mobileNumber + (hasReplyFlowIdId ? message.getSerialNumber() : "");
        SyncFuture receive = messageManager.receive(key);
        try {
            return receive.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            messageManager.remove(key);
            e.printStackTrace();
        }
        return null;
    }


    @Mapping(types = 终端通用应答, desc = "终端通用应答")
    public void 终端通用应答(Message<CommonResult> message) {
        CommonResult body = message.getBody();
        String mobileNumber = message.getMobileNumber();
        Integer replyId = body.getReplyId();
        messageManager.put(mobileNumber + replyId, message);
    }
    //=============================================================

    @Mapping(types = 终端心跳, desc = "终端心跳")
    public Message heartBeat(Message message, Session session) {
        CommonResult result = new CommonResult(终端心跳, message.getSerialNumber(), CommonResult.Success);
        return new Message(平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = 终端注册, desc = "终端注册")
    public Message<RegisterResult> register(Message<Register> message, Session session) {
        session.setUserAlias(message.getMobileNumber());
        sessionManager.put(session.getId(), session);

        // 发送到redis
        jt808Sender.send(message, session.getId(), ProtocolBusiness.AntIM);
        return null;
    }

    @Mapping(types = 终端注销, desc = "终端注销")
    public Message 终端注销(Message message, Session session) {
        //TODO
        CommonResult result = new CommonResult(终端注销, message.getSerialNumber(), CommonResult.Success);
        return new Message(平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = 终端鉴权, desc = "终端鉴权")
    public Message authentication(Message<Authentication> message, Session session) {
        session.setUserAlias(message.getMobileNumber());
        sessionManager.put(session.getId(), session);

        // 发送到redis
        jt808Sender.send(message, session.getId(), ProtocolBusiness.AntIM);
        return null;
    }

    @Mapping(types = IM消息, desc = "IM消息")
    public Message imMsg(Message<IMMsg> message, Session session) {
        session.setUserAlias(message.getMobileNumber());
        sessionManager.put(session.getId(), session);

        // 发送到redis
        jt808Sender.send(message, session.getId(), ProtocolBusiness.AntIM);
        return null;
    }

}