package com.ant.msger.main.web.jt808.codec;

import com.ant.msger.base.message.AbstractBody;
import com.ant.msger.base.message.AbstractMessage;
import com.ant.msger.main.framework.mapping.Handler;
import com.ant.msger.main.framework.mapping.HandlerMapper;
import com.ant.msger.main.framework.redis.RedisFragMsgService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 808协议解码器
 *
 * @author zhihao.ye (yezhihaoo@gmail.com)
 */
public class JT808MessageTcpDecoder extends ByteToMessageDecoder {
    private HandlerMapper handlerMapper;
    private RedisFragMsgService fragMsgService;
    private JT808MessageBaseDecoder baseDecoder = new JT808MessageBaseDecoder();

    public JT808MessageTcpDecoder() {
    }

    public JT808MessageTcpDecoder(HandlerMapper handlerMapper, RedisFragMsgService fragMsgService) {
        this.handlerMapper = handlerMapper;
        this.fragMsgService = fragMsgService;
    }

@Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        // 将输入转换为bean
        String clientSign = channelHandlerContext.channel().id().asLongText();
        AbstractMessage<? extends AbstractBody> message = baseDecoder.hexStringToBean(clientSign, in, handlerMapper, fragMsgService);
        if (null == message) {
            return;
        }
        out.add(message);

        in.skipBytes(in.readableBytes());
    }
}