package com.ant.msger.main.framework.handler;

import com.ant.msger.base.dto.jt808.CommonResult;
import com.ant.msger.base.dto.jt808.basics.Message;
import com.ant.msger.base.message.AbstractMessage;
import com.ant.msger.main.framework.log.Logger;
import com.ant.msger.main.framework.mapping.Handler;
import com.ant.msger.main.framework.mapping.HandlerMapper;
import com.ant.msger.main.framework.session.Session;
import com.ant.msger.main.framework.session.SessionManager;
import com.ant.msger.main.web.config.SessionKey;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;

import static com.ant.msger.base.common.MessageId.平台通用应答;

public class BaseHandler extends ChannelInboundHandlerAdapter {
    protected final SessionManager sessionManager = SessionManager.getInstance();

    protected Logger logger;
    protected Integer sessionMinutes;

    protected HandlerMapper handlerMapper;

    protected AbstractMessage consumerMessage(Protocol protocol, AbstractMessage messageRequest, InetSocketAddress socketAddress, Session session) throws java.lang.reflect.InvocationTargetException, IllegalAccessException {
        Handler handler = handlerMapper.getHandler(messageRequest.getDelimiter(), messageRequest.getType());
        Type[] types = handler.getTargetParameterTypes();

        AbstractMessage messageResponse;
        if (types.length == 1) { //handler.targetMethod.getName().equals("register")
            messageResponse = handler.invoke(messageRequest);
        } else if (StringUtils.equals("register", handler.getTargetMethod().getName())
            || StringUtils.equals("authentication", handler.getTargetMethod().getName())) {
            if (protocol == Protocol.UDP) {
                session = initUdpSession((Message) messageRequest, socketAddress);
            }
            messageResponse = handler.invoke(messageRequest, session);
        } else {
            session = sessionManager.getByMobileNumber(getMobileNum(messageRequest));
            // session是否过期了，过期了直接返回失败
            if (null == session
                    || System.currentTimeMillis() - session.getLastCommunicateTimeStamp() > 1000 * 60 * sessionMinutes) {
                // 通用失败应答
                CommonResult result = new CommonResult(messageRequest.getType(), ((Message)messageRequest).getSerialNumber(), CommonResult.Fial);
                // 连接已丢失，未重连前，返回的序列号全为1
                messageResponse = new Message(平台通用应答, 1, ((Message)messageRequest).getMobileNumber(), result);
            } else {
                session.setLastCommunicateTimeStamp(System.currentTimeMillis());
                messageResponse = handler.invoke(messageRequest, session);
            }
        }
        return messageResponse;
    }

    private Session initUdpSession(Message message, InetSocketAddress socketAddress) {
        Session session = new Session();
        session.setTerminalId(message.getMobileNumber());
        session.setId(message.getMobileNumber());
        session.setSocketAddress(socketAddress);
        session.setProtocol(Protocol.UDP);
        session.setChannel(sessionManager.getBySessionId(SessionKey.UDP_GLOBAL_CHANNEL_KEY).getChannel());
        session.setLastCommunicateTimeStamp(System.currentTimeMillis());
        return session;
    }

    private String getMobileNum(AbstractMessage messageRequest) {
        Message message = (Message) messageRequest;
        return message.getMobileNumber();
    }
}
