package com.antnest.msger.main.framework.handler;

import com.antnest.msger.core.dto.jt808.basics.Message;
import com.antnest.msger.core.message.AbstractMessage;
import com.antnest.msger.main.framework.commons.enumeration.ProtocolCommunication;
import com.antnest.msger.main.framework.log.Logger;
import com.antnest.msger.core.mapping.HandlerMapper;
import com.antnest.msger.main.framework.session.Session;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import java.net.InetSocketAddress;

public class TCPServerHandler extends BaseHandler {

    public TCPServerHandler(HandlerMapper handlerMapper, int sessionMinutes) {
        this.handlerMapper = handlerMapper;
        this.sessionMinutes = sessionMinutes;
        this.logger = new Logger();
    }

    public TCPServerHandler(HandlerMapper handlerMapper, int sessionMinutes, Logger logger) {
        this.handlerMapper = handlerMapper;
        this.sessionMinutes = sessionMinutes;
        this.logger = logger;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 分段消息时的特殊处理
        if (!(msg instanceof AbstractMessage)) {
            return;
        }

        try {
            AbstractMessage messageRequest = (AbstractMessage) msg;
            Channel channel = ctx.channel();
            InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
            Session session = sessionManager.getBySessionId(Session.buildId(channel));

            // 消息事件处理
            AbstractMessage messageResponse = consumerMessage(ProtocolCommunication.TCP, messageRequest, socketAddress, session);

            if (messageResponse != null) {
                protocolMsgSender.sendTcpMsgSingle(session, (Message) messageResponse);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Session session = Session.buildSession(ctx.channel());
        session.setProtocolCommunication(ProtocolCommunication.TCP);
        sessionManager.put(session.getId(), session);
        logger.logEvent("TCP终端连接", session);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        String sessionId = Session.buildId(ctx.channel());
        Session session = sessionManager.removeBySessionId(sessionId);
        logger.logEvent("TCP断开连接", session);
        ctx.channel().close();
        // ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        String sessionId = Session.buildId(ctx.channel());
        Session session = sessionManager.getBySessionId(sessionId);
        logger.logEvent("TCP发生异常", session);
        e.printStackTrace();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                Session session = this.sessionManager.removeBySessionId(Session.buildId(ctx.channel()));
                logger.logEvent("TCP服务器主动断开连接", session);
                ctx.close();
            }
        }
    }
}