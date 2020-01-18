package com.ant.msger.main.web.jt808.codec;

import com.ant.msger.base.message.AbstractBody;
import com.ant.msger.base.message.AbstractMessage;
import com.ant.msger.main.framework.mapping.Handler;
import com.ant.msger.main.framework.mapping.HandlerMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.util.List;

/**
 * 808协议解码器
 *
 * @author zhihao.ye (yezhihaoo@gmail.com)
 */
//public class JT808MessageWebsocketDecoder extends StringDecoder {
public class JT808MessageWebsocketDecoder extends MessageToMessageDecoder {
    private HandlerMapper handlerMapper;
    private JT808MessageBaseDecoder baseDecoder = new JT808MessageBaseDecoder();

    public JT808MessageWebsocketDecoder() {
    }

    public JT808MessageWebsocketDecoder(HandlerMapper handlerMapper) {
        this.handlerMapper = handlerMapper;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, Object o, List out) throws Exception {
        ByteBuf in = (ByteBuf) o;

        // 去掉首尾的7e标识，如果没有标识，则认为消息不对，直接不处理了
        in = baseDecoder.checkAndRemove7E(in);
        if (in == null) {
            return;
        }

        int type = baseDecoder.getType(in);
        Handler handler = handlerMapper.getHandler(type);

        if (handler == null) {
            return;
        }

        // 将输入转换为bean
        AbstractMessage<? extends AbstractBody> message = baseDecoder.decodeIn2Message(in, handler);
        out.add(message);

        in.skipBytes(in.readableBytes());
    }

//
////    @Override
//    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
//        int type = baseDecoder.getType(in);
//        Handler handler = handlerMapper.getHandler(type);
//
//        if (handler == null) {
//            return;
//        }
//
//        // 将输入转换为bean
//        AbstractMessage<? extends AbstractBody> message = baseDecoder.decodeIn2Message(in, handler);
//        out.add(message);
//
//        in.skipBytes(in.readableBytes());
//    }
}