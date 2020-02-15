package com.ant.msger.main.framework.handler;

import com.ant.msger.base.dto.jt808.basics.Message;
import com.ant.msger.base.message.AbstractMessage;
import com.ant.msger.main.framework.commons.enumeration.ProtocolCommunication;
import com.ant.msger.main.framework.log.Logger;
import com.ant.msger.main.framework.mapping.HandlerMapper;
import com.ant.msger.main.framework.session.Session;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import java.net.InetSocketAddress;

public class WebsocketServerHandler extends BaseHandler {

    public WebsocketServerHandler(HandlerMapper handlerMapper, int sessionMinutes) {
        this.handlerMapper = handlerMapper;
        this.sessionMinutes = sessionMinutes;
        this.logger = new Logger();
    }

    public WebsocketServerHandler(HandlerMapper handlerMapper, int sessionMinutes, Logger logger) {
        this.handlerMapper = handlerMapper;
        this.sessionMinutes = sessionMinutes;
        this.logger = logger;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            AbstractMessage messageRequest = (AbstractMessage) msg;
            Channel channel = ctx.channel();
            InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
            Session session = sessionManager.getBySessionId(Session.buildId(channel));

            // 消息事件处理
            AbstractMessage messageResponse = consumerMessage(ProtocolCommunication.WEBSOCKET, messageRequest, socketAddress, session);

            if (messageResponse != null) {
                protocolMsgSender.sendWebsocketMsgSingle(session, (Message) messageResponse);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Session session = Session.buildSession(ctx.channel());
        session.setProtocolCommunication(ProtocolCommunication.WEBSOCKET);
        sessionManager.put(session.getId(), session);
        logger.logEvent("WEBSOCKET终端连接", session);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        String sessionId = Session.buildId(ctx.channel());
        Session session = sessionManager.removeBySessionId(sessionId);
        logger.logEvent("WEBSOCKET断开连接", session);
        ctx.channel().close();
        // ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        String sessionId = Session.buildId(ctx.channel());
        Session session = sessionManager.getBySessionId(sessionId);
        logger.logEvent("WEBSOCKET发生异常", session);
        e.printStackTrace();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                Session session = this.sessionManager.removeBySessionId(Session.buildId(ctx.channel()));
                logger.logEvent("WEBSOCKET服务器主动断开连接", session);
                ctx.close();
            }
        }
    }
}