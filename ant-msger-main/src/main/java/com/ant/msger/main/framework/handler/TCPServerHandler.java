package com.ant.msger.main.framework.handler;

import com.ant.msger.base.message.AbstractMessage;
import com.ant.msger.main.framework.log.Logger;
import com.ant.msger.main.framework.mapping.Handler;
import com.ant.msger.main.framework.mapping.HandlerMapper;
import com.ant.msger.main.framework.session.Session;
import com.ant.msger.main.framework.session.SessionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;

public class TCPServerHandler extends BaseHandler {

    public TCPServerHandler(HandlerMapper handlerMapper) {
        this.handlerMapper = handlerMapper;
        this.logger = new Logger();
    }

    public TCPServerHandler(HandlerMapper handlerMapper, Logger logger) {
        this.handlerMapper = handlerMapper;
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
            AbstractMessage messageResponse = consumerMessage(Protocol.TCP, messageRequest, socketAddress, session);

            if (messageResponse != null) {
                ChannelFuture future = channel.writeAndFlush(messageResponse).sync();
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Session session = Session.buildSession(ctx.channel());
        session.setProtocol(Protocol.TCP);
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