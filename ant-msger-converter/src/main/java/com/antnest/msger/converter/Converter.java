package com.antnest.msger.converter;

import com.ant.msger.base.dto.jt808.basics.Message;
import com.ant.msger.base.message.AbstractBody;
import com.antnest.msger.converter.codec.JT808MessageBaseDecoder;
import com.antnest.msger.converter.codec.JT808MessageEncoder;
import com.antnest.msger.converter.mapping.HandlerMapper;
import com.antnest.msger.converter.mapping.HandlerMapperHolder;
import com.antnest.msger.converter.util.HexUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.StringUtils;

/**
 * JT808报文和bean的转换器
 */
public class Converter {

    private static final HandlerMapper handlerMapper = HandlerMapperHolder.getInstance();
    private static final JT808MessageBaseDecoder decoder = new JT808MessageBaseDecoder();
    private static final JT808MessageEncoder encoder = new JT808MessageEncoder();

    public static <T extends AbstractBody> Message<T> jt808MsgToBean(String jt808Msg) {
        jt808Msg = jt808Msg.toLowerCase();
        ByteBuf buf = Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(jt808Msg));
        Message<T> bean = (Message<T>) decoder.hexStringToBean(buf, handlerMapper);
        return bean;
    }

    public static <T extends AbstractBody> String beanToJt808Msg(Message<T> bean) {
        ByteBuf buf = encoder.encode(bean);
        String hex = ByteBufUtil.hexDump(buf);
        String delimiterStr = HexUtil.intTohex(bean.getDelimiter()).toLowerCase();
        return delimiterStr.concat(hex).concat(delimiterStr);
    }
}
