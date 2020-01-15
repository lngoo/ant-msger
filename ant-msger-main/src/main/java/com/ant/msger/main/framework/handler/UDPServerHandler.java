package com.ant.msger.main.framework.handler;

import com.ant.msger.base.message.AbstractMessage;
import com.ant.msger.main.framework.mapping.HandlerMapper;
import com.ant.msger.main.framework.session.Session;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import com.ant.msger.main.framework.commons.bean.DecodeResult;
import com.ant.msger.main.framework.log.Logger;
import com.ant.msger.main.web.config.SessionKey;

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

            // 消息事件处理
            AbstractMessage messageResponse = consumerMessage(messageRequest, socketAddress, null);

            if (messageResponse != null) {
                Channel channel = ctx.channel();
                channel.connect(((DecodeResult) msg).getDatagramPacket().sender());
                ChannelFuture future = channel.writeAndFlush(messageResponse).sync();
                channel.disconnect();
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

//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) {
//        String sessionId = Session.buildId(ctx.channel());
//        Session session = sessionManager.removeBySessionId(sessionId);
//        logger.logEvent("断开连接", session);
//        ctx.channel().close();
//        // ctx.close();
//    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
//        String sessionId = Session.buildId(ctx.channel());
//        Session session = sessionManager.getBySessionId(sessionId);
//        logger.logEvent("发生异常", session);
//        e.printStackTrace();
//    }
//
//    @Override
//    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
//        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
//            IdleStateEvent event = (IdleStateEvent) evt;
//            if (event.state() == IdleState.READER_IDLE) {
//                Session session = this.sessionManager.removeBySessionId(Session.buildId(ctx.channel()));
//                logger.logEvent("服务器主动断开连接", session);
//                ctx.close();
//            }
//        }
//    }
}