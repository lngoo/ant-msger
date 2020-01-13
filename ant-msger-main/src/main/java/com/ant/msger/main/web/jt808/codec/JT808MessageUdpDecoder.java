package com.ant.msger.main.web.jt808.codec;

import com.ant.msger.base.message.AbstractBody;
import com.ant.msger.base.message.AbstractMessage;
import com.ant.msger.main.framework.commons.bean.DecodeResult;
import com.ant.msger.main.framework.mapping.Handler;
import com.ant.msger.main.framework.mapping.HandlerMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.DatagramPacketDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * 808协议解码器
 *
 * @author zhihao.ye (yezhihaoo@gmail.com)
 */
public class JT808MessageUdpDecoder extends DatagramPacketDecoder {

    private HandlerMapper handlerMapper;

    private JT808MessageBaseDecoder baseDecoder = new JT808MessageBaseDecoder();

    public JT808MessageUdpDecoder(MessageToMessageDecoder<ByteBuf> decoder) {
        super(decoder);
    }

    public JT808MessageUdpDecoder(MessageToMessageDecoder<ByteBuf> decoder, HandlerMapper handlerMapper) {
        super(decoder);
        this.handlerMapper = handlerMapper;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) {
        ByteBuf in = msg.content();

        // 去掉首尾的7e标识，如果没有标识，则认为消息不对，直接不处理了
        in = checkAndRemove7E(in);
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
        DecodeResult decodeResult = new DecodeResult(message, msg);
        out.add(decodeResult);

        in.skipBytes(in.readableBytes());
    }

    private ByteBuf checkAndRemove7E(ByteBuf in) {
        int stand = Integer.parseInt("7E", 16);
        byte[] fristByte = ByteBufUtil.getBytes(in, 0, 1);
        byte[] endByte = ByteBufUtil.getBytes(in, in.readableBytes() - 1, 1);
        if (stand == fristByte[0]
                && stand == endByte[0]) {
            return in.slice(1, in.readableBytes() - 2);
        } else {
            return null;
        }
    }
}