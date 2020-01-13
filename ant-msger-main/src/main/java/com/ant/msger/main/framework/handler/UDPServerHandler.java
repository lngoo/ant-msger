package com.ant.msger.main.framework.handler;

import com.ant.msger.base.dto.jt808.CommonResult;
import com.ant.msger.base.dto.jt808.basics.Message;
import com.ant.msger.base.message.AbstractMessage;
import com.ant.msger.main.framework.handler.BaseHandler;
import com.ant.msger.main.framework.mapping.Handler;
import com.ant.msger.main.framework.mapping.HandlerMapper;
import com.ant.msger.main.framework.session.Session;
import com.ant.msger.main.framework.session.SessionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.lang3.StringUtils;
import com.ant.msger.main.framework.commons.bean.DecodeResult;
import com.ant.msger.main.framework.log.Logger;
import com.ant.msger.main.web.config.SessionKey;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;

import static com.ant.msger.base.common.MessageId.平台通用应答;

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
            AbstractMessage messageResponse = consumerMessage(messageRequest, socketAddress);

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
        sessionManager.put(SessionKey.GLOBAL_CHANNEL_KEY, session);
        logger.logEvent("终端连接", session);
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