package org.yzh.framework;

import com.ant.jt808.base.dto.jt808.CommonResult;
import com.ant.jt808.base.dto.jt808.basics.Message;
import com.ant.jt808.base.message.AbstractMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.lang3.StringUtils;
import org.yzh.framework.commons.bean.DecodeResult;
import org.yzh.framework.log.Logger;
import org.yzh.framework.mapping.Handler;
import org.yzh.framework.mapping.HandlerMapper;
import org.yzh.framework.session.Session;
import org.yzh.framework.session.SessionManager;
import org.yzh.web.config.SessionKey;

import java.lang.reflect.Type;

import static com.ant.jt808.base.common.MessageId.平台通用应答;
import static com.ant.jt808.base.common.MessageId.终端心跳;

public class UDPServerHandler extends ChannelInboundHandlerAdapter {

    private final SessionManager sessionManager = SessionManager.getInstance();

    private Logger logger;

    private HandlerMapper handlerMapper;

    public UDPServerHandler(HandlerMapper handlerMapper) {
        this.handlerMapper = handlerMapper;
        this.logger = new Logger();
    }

    public UDPServerHandler(HandlerMapper handlerMapper, Logger logger) {
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

            AbstractMessage messageResponse;
            if (types.length == 1) {
                messageResponse = handler.invoke(messageRequest);
            } else if (StringUtils.equals(types[1].getTypeName(), DatagramPacket.class.getName())) {
                messageResponse = handler.invoke(messageRequest, decodeResult.getDatagramPacket());
            } else {
                Session session = sessionManager.getByMobileNumber(getMobileNum(messageRequest));
                // 5分钟过期,过期了直接返回失败
                if (null == session
                        || System.currentTimeMillis() - session.getLastCommunicateTimeStamp() > 1000 * 60 * 5) {
                    // 通用失败应答
                    CommonResult result = new CommonResult(messageRequest.getType(), ((Message)messageRequest).getSerialNumber(), CommonResult.Fial);
                    // 连接已丢失，未重连前，返回的序列号全为1
                    messageResponse = new Message(平台通用应答, 1, ((Message)messageRequest).getMobileNumber(), result);
                } else {
                    session.setLastCommunicateTimeStamp(System.currentTimeMillis());
                    messageResponse = handler.invoke(messageRequest, session);
                }
            }

            if (messageResponse != null) {
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