package com.antnest.msger.main.web.jt808.codec;

import com.antnest.msger.core.codec.JT808MessageBaseDecoder;
import com.antnest.msger.core.message.AbstractBody;
import com.antnest.msger.core.message.AbstractMessage;
import com.antnest.msger.main.framework.commons.bean.DecodeResult;
import com.antnest.msger.core.mapping.HandlerMapper;
import com.antnest.msger.core.redis.RedisFragMsgService;
import io.netty.buffer.ByteBuf;
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
    private RedisFragMsgService fragMsgService;

    private JT808MessageBaseDecoder baseDecoder = new JT808MessageBaseDecoder();

    public JT808MessageUdpDecoder(MessageToMessageDecoder<ByteBuf> decoder) {
        super(decoder);
    }

    public JT808MessageUdpDecoder(MessageToMessageDecoder<ByteBuf> decoder, HandlerMapper handlerMapper, RedisFragMsgService fragMsgService) {
        super(decoder);
        this.handlerMapper = handlerMapper;
        this.fragMsgService = fragMsgService;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) {
        ByteBuf in = msg.content();

        // 将输入转换为bean
        String clientSign = "udp_" + msg.sender().getHostName() + "_" + msg.sender().getPort();
        AbstractMessage<? extends AbstractBody> message = baseDecoder.hexStringToBean(clientSign, in, handlerMapper, fragMsgService);
        if (null == message) {
            return;
        }

        DecodeResult decodeResult = new DecodeResult(message, msg);
        out.add(decodeResult);

        in.skipBytes(in.readableBytes());
    }
}