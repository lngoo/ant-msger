package com.ant.msger.main.framework.handler;

import com.ant.msger.base.dto.jt808.CommonResult;
import com.ant.msger.base.dto.jt808.basics.Message;
import com.ant.msger.base.message.AbstractMessage;
import com.ant.msger.main.framework.log.Logger;
import com.ant.msger.main.framework.mapping.Handler;
import com.ant.msger.main.framework.mapping.HandlerMapper;
import com.ant.msger.main.framework.session.Session;
import com.ant.msger.main.framework.session.SessionManager;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;

import static com.ant.msger.base.common.MessageId.平台通用应答;

public class BaseHandler extends ChannelInboundHandlerAdapter {
    protected final SessionManager sessionManager = SessionManager.getInstance();

    protected Logger logger;
    protected int sessionMinutes;

    protected HandlerMapper handlerMapper;

    protected AbstractMessage consumerMessage(AbstractMessage messageRequest, InetSocketAddress socketAddress) throws java.lang.reflect.InvocationTargetException, IllegalAccessException {
        Handler handler = handlerMapper.getHandler(messageRequest.getType());
        Type[] types = handler.getTargetParameterTypes();

        AbstractMessage messageResponse;
        if (types.length == 1) {
            messageResponse = handler.invoke(messageRequest);
        } else if (StringUtils.equals(types[1].getTypeName(), InetSocketAddress.class.getName())) {
            messageResponse = handler.invoke(messageRequest, socketAddress);
        } else {
            Session session = sessionManager.getByMobileNumber(getMobileNum(messageRequest));
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

    private String getMobileNum(AbstractMessage messageRequest) {
        Message message = (Message) messageRequest;
        return message.getMobileNumber();
    }
}
