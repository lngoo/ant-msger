package com.ant.msger.main.web.jt808.codec;

import com.ant.msger.base.message.AbstractBody;
import com.ant.msger.base.message.AbstractMessage;
import com.ant.msger.main.framework.mapping.Handler;
import com.ant.msger.main.framework.mapping.HandlerMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class JT808MessageTcpDecoder1 extends ByteToMessageDecoder {

    private HandlerMapper handlerMapper;
    private JT808MessageBaseDecoder baseDecoder = new JT808MessageBaseDecoder();

    public JT808MessageTcpDecoder1() {
    }

    public JT808MessageTcpDecoder1(HandlerMapper handlerMapper) {
        this.handlerMapper = handlerMapper;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
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
}
