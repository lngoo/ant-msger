package com.ant.msger.main.framework.codec;

import com.ant.msger.base.annotation.Property;
import com.ant.msger.base.enums.DataType;
import com.ant.msger.base.message.AbstractBody;
import com.ant.msger.base.message.AbstractMessage;
import com.ant.msger.main.framework.commons.PropertyUtils;
import com.ant.msger.main.framework.commons.bean.BeanUtils;
import com.ant.msger.main.framework.commons.bean.DecodeResult;
import com.ant.msger.main.framework.commons.transform.Bcd;
import com.ant.msger.main.framework.mapping.Handler;
import com.ant.msger.main.framework.mapping.HandlerMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.DatagramPacketDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.ant.msger.base.enums.DataType.*;


/**
 * 基础消息解码
 *
 * @author zhihao.ye (yezhihaoo@gmail.com)
 * @home http://gitee.com/yezhihao/msger-server
 */
public abstract class MessageUdpDecoder extends DatagramPacketDecoder {

    private HandlerMapper handlerMapper;

    public MessageUdpDecoder(MessageToMessageDecoder<ByteBuf> decoder) {
        super(decoder);
    }

    public MessageUdpDecoder(MessageToMessageDecoder<ByteBuf> decoder, HandlerMapper handlerMapper) {
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

        int type = getType(in);
        Handler handler = handlerMapper.getHandler(type);

        if (handler == null) {
            return;
        }

        Type[] types = handler.getTargetParameterTypes();
        AbstractMessage<? extends AbstractBody> decode = null;
        if (types[0] instanceof ParameterizedTypeImpl) {
            ParameterizedTypeImpl clazz = (ParameterizedTypeImpl) types[0];

            Class<? extends AbstractBody> bodyClass = (Class<? extends AbstractBody>) clazz.getActualTypeArguments()[0];
            Class<? extends AbstractMessage> messageClass = (Class<? extends AbstractMessage>) clazz.getRawType();
            decode = decode(in, messageClass, bodyClass);
        } else {
            decode = decode(in, (Class) types[0], null);
        }
        DecodeResult decodeResult = new DecodeResult(decode, msg);
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

//    @Override
//    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
//        decode(ctx, msg.content(), out);
//    }
//
//    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
////        in.
//        int type = getType(in);
//        Handler handler = handlerMapper.getHandler(type);
//
//        if (handler == null) {
//            return;
//        }
//
//        Type[] types = handler.getTargetParameterTypes();
//        if (types[0] instanceof ParameterizedTypeImpl) {
//            ParameterizedTypeImpl clazz = (ParameterizedTypeImpl) types[0];
//
//            Class<? extends AbstractBody> bodyClass = (Class<? extends AbstractBody>) clazz.getActualTypeArguments()[0];
//            Class<? extends AbstractMessage> messageClass = (Class<? extends AbstractMessage>) clazz.getRawType();
//            AbstractMessage<? extends AbstractBody> decode = decode(in, messageClass, bodyClass);
//            out.add(decode);
//        } else {
//            AbstractMessage<? extends AbstractBody> decode = decode(in, (Class) types[0], null);
//            out.add(decode);
//        }
//
//        in.skipBytes(in.readableBytes());
//    }

    /**
     * 解析
     */
    public <T extends AbstractBody> AbstractMessage<T> decode(ByteBuf buf, Class<? extends AbstractMessage> clazz, Class<T> bodyClass) {
        buf = unEscape(buf);

        if (check(buf))
            System.out.println("校验码错误" + ByteBufUtil.hexDump(buf));

        AbstractMessage message = decode(buf, clazz);
        if (bodyClass != null) {
            Integer headerLength = message.getHeaderLength();
            buf.setIndex(headerLength, headerLength + message.getBodyLength());
            T body = decode(buf, bodyClass);
            message.setBody(body);
        }
        return message;
    }

    /**
     * 获取消息类型
     */
    public abstract int getType(ByteBuf buf);

    /**
     * 反转义
     */
    public abstract ByteBuf unEscape(ByteBuf buf);

    /**
     * 校验
     */
    public abstract boolean check(ByteBuf buf);

    public <T> T decode(ByteBuf buf, Class<T> targetClass) {
        T result = BeanUtils.newInstance(targetClass);

        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptor(targetClass);
        for (PropertyDescriptor pd : pds) {

            Method readMethod = pd.getReadMethod();
            Property prop = readMethod.getDeclaredAnnotation(Property.class);
            int length = PropertyUtils.getLength(result, prop);
            if (!buf.isReadable(length))
                break;

            if (length == -1)
                length = buf.readableBytes();
            Object value = null;
            try {
                value = read(buf, prop, length, pd);
            } catch (Exception e) {
                e.printStackTrace();
            }
            BeanUtils.setValue(result, pd.getWriteMethod(), value);
        }
        return result;
    }

    public Object read(ByteBuf buf, Property prop, int length, PropertyDescriptor pd) {
        DataType type = prop.type();

        if (type == BYTE) {
            return (int) buf.readUnsignedByte();
        } else if (type == WORD) {
            return buf.readUnsignedShort();
        } else if (type == DWORD) {
            if (pd.getPropertyType().isAssignableFrom(Long.class))
                return buf.readUnsignedInt();
            return (int) buf.readUnsignedInt();
        } else if (type == STRING) {
            return buf.readCharSequence(length, Charset.forName(prop.charset())).toString().trim();
        } else if (type == OBJ) {
            return decode(buf.readSlice(length), pd.getPropertyType());
        } else if (type == LIST) {
            List list = new ArrayList();
            Type clazz = ((ParameterizedType) pd.getReadMethod().getGenericReturnType()).getActualTypeArguments()[0];
            ByteBuf slice = buf.readSlice(length);
            while (slice.isReadable())
                list.add(decode(slice, (Class) clazz));
            return list;
        }

        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        if (type == BCD8421)
            return Bcd.encode8421String(bytes).trim();
        return bytes;
    }
}