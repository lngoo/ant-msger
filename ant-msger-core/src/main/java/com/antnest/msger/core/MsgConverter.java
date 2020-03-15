package com.antnest.msger.core;

import com.antnest.msger.core.codec.JT808MessageBaseDecoder;
import com.antnest.msger.core.codec.MsgSplitterEncoder;
import com.antnest.msger.core.dto.jt808.basics.Message;
import com.antnest.msger.core.mapping.HandlerMapper;
import com.antnest.msger.core.message.AbstractBody;
import com.antnest.msger.core.message.AbstractMessage;
import com.antnest.msger.core.redis.RedisFragMsgService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * JT808消息与message之间的转换器
 */
@Service
public class MsgConverter {

    @Autowired
    private HandlerMapper handlerMapper;

    @Autowired
    private RedisFragMsgService fragMsgService;

    private static final JT808MessageBaseDecoder decoder = new JT808MessageBaseDecoder();
    private static final MsgSplitterEncoder encoder = new MsgSplitterEncoder();

//    private static final HandlerMapper handlerMapper = HandlerMapperHolder.getInstance();

    public <T extends AbstractBody> Message<T> jt808MsgToBean(String clientSign, String jt808Msg) {
        jt808Msg = jt808Msg.toLowerCase();
        ByteBuf buf = Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(jt808Msg));

        AbstractMessage<T> bean = (AbstractMessage<T>) decoder.hexStringToBean(clientSign, buf, handlerMapper, fragMsgService);
        return (Message<T>) bean;
    }

    public <T extends AbstractBody> List<String> beanToJt808Msg(Message<T> bean) {
        return encoder.splitAndEncode(bean);
    }
}
