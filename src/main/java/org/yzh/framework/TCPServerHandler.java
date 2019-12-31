package org.yzh.framework;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.oio.OioDatagramChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.lang3.StringUtils;
import org.yzh.framework.commons.bean.DecodeResult;
import org.yzh.framework.log.Logger;
import org.yzh.framework.mapping.Handler;
import org.yzh.framework.mapping.HandlerMapper;
import org.yzh.framework.message.AbstractBody;
import org.yzh.framework.message.AbstractMessage;
import org.yzh.framework.session.Session;
import org.yzh.framework.session.SessionManager;
import org.yzh.web.jt808.dto.basics.Message;

import java.lang.reflect.Type;

public class TCPServerHandler extends ChannelInboundHandlerAdapter {

    private final SessionManager sessionManager = SessionManager.getInstance();

    private Logger logger;

    private HandlerMapper handlerMapper;

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
            DecodeResult decodeResult = (DecodeResult) msg;
            AbstractMessage messageRequest = decodeResult.getMessage();
            Channel channel = ctx.channel();

            Handler handler = handlerMapper.getHandler(messageRequest.getType());

            Type[] types = handler.getTargetParameterTypes();
            Session session = sessionManager.getByMobileNumber(getMobileNum(messageRequest));

            AbstractMessage messageResponse;
            if (types.length == 1) {
                messageResponse = handler.invoke(messageRequest);
            } else if (StringUtils.equals(types[1].getTypeName(), DatagramPacket.class.getName())) {
                messageResponse = handler.invoke(messageRequest, decodeResult.getDatagramPacket());
            } else {
                messageResponse = handler.invoke(messageRequest, session);
            }

            if (messageResponse != null) {
//                DatagramPacket data = new DatagramPacket(Unpooled.copiedBuffer(messageResponse), decodeResult.getDatagramPacket().sender());
//                ctx.writeAndFlush(data);//向客户端发送消息
//                ChannelFuture future = (channel).writeAndFlush(messageResponse).sync();
                channel.connect(((DecodeResult) msg).getDatagramPacket().sender());
                ChannelFuture future = channel.writeAndFlush(messageResponse).sync();
                channel.disconnect();
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private String getMobileNum(AbstractMessage messageRequest) {
        Message message = (Message) messageRequest;
        return message.getMobileNumber();
    }

//    @Override
//    public void channelActive(ChannelHandlerContext ctx) {
//        Session session = Session.buildSession(ctx.channel());
//        sessionManager.put(session.getId(), session);
//        logger.logEvent("终端连接", session);
//    }
//
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