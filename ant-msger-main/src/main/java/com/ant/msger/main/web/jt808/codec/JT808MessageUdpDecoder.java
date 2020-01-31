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

        // 将输入转换为bean
        AbstractMessage<? extends AbstractBody> message = baseDecoder.hexStringToBean(in, handlerMapper);
        if (null == message) {
            return;
        }

        DecodeResult decodeResult = new DecodeResult(message, msg);
        out.add(decodeResult);

        in.skipBytes(in.readableBytes());
    }
}