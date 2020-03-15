package com.antnest.msger.main.framework.handler;

import com.antnest.msger.core.dto.jt808.basics.Message;
import com.antnest.msger.core.message.AbstractMessage;
import com.antnest.msger.main.framework.commons.enumeration.ProtocolCommunication;
import com.antnest.msger.core.mapping.HandlerMapper;
import com.antnest.msger.main.framework.session.Session;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import com.antnest.msger.main.framework.commons.bean.DecodeResult;
import com.antnest.msger.main.framework.log.Logger;
import com.antnest.msger.main.web.config.SessionKey;

import java.net.InetSocketAddress;

public class UDPServerHandler extends BaseHandler {

    public UDPServerHandler(HandlerMapper handlerMapper, int sessionMinutes) {
        this.handlerMapper = handlerMapper;
        this.sessionMinutes = sessionMinutes;
        this.logger = new Logger();
    }

    public UDPServerHandler(HandlerMapper handlerMapper, int sessionMinutes, Logger logger) {
        this.handlerMapper = handlerMapper;
        this.sessionMinutes = sessionMinutes;
        this.logger = logger;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            DecodeResult decodeResult = (DecodeResult) msg;
            AbstractMessage messageRequest = decodeResult.getMessage();
            InetSocketAddress socketAddress = decodeResult.getDatagramPacket().sender();
            Session session = sessionManager.getBySessionId(Session.buildId(socketAddress));

            // 消息事件处理
            AbstractMessage messageResponse = consumerMessage(ProtocolCommunication.UDP, messageRequest, socketAddress, session);

            if (messageResponse != null) {
                protocolMsgSender.sendUdpMsgSingle(session, (Message) messageResponse);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Session session = new Session();
        session.setChannel(ctx.channel());
        sessionManager.put(SessionKey.UDP_GLOBAL_CHANNEL_KEY, session);
        logger.logEvent("UDP终端连接", session);
    }

    private String getMobileNum(AbstractMessage messageRequest) {
        Message message = (Message) messageRequest;
        return message.getMobileNumber();
    }

//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) {
//        String sessionId = Session.buildId(ctx.channel());
//        Session persistence = sessionManager.removeBySessionId(sessionId);
//        logger.logEvent("断开连接", persistence);
//        ctx.channel().close();
//        // ctx.close();
//    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
//        String sessionId = Session.buildId(ctx.channel());
//        Session persistence = sessionManager.getBySessionId(sessionId);
//        logger.logEvent("发生异常", persistence);
//        e.printStackTrace();
//    }
//
//    @Override
//    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
//        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
//            IdleStateEvent event = (IdleStateEvent) evt;
//            if (event.state() == IdleState.READER_IDLE) {
//                Session persistence = this.sessionManager.removeBySessionId(Session.buildId(ctx.channel()));
//                logger.logEvent("服务器主动断开连接", persistence);
//                ctx.close();
//            }
//        }
//    }
}