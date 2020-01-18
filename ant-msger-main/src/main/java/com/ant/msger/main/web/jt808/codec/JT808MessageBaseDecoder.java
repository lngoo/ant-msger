package com.ant.msger.main.web.jt808.codec;

import com.ant.msger.base.annotation.Property;
import com.ant.msger.base.enums.DataType;
import com.ant.msger.base.message.AbstractBody;
import com.ant.msger.base.message.AbstractMessage;
import com.ant.msger.main.framework.commons.PropertyUtils;
import com.ant.msger.main.framework.commons.bean.BeanUtils;
import com.ant.msger.main.framework.commons.transform.Bcd;
import com.ant.msger.main.framework.commons.transform.ByteBufUtils;
import com.ant.msger.main.framework.mapping.Handler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.ant.msger.base.enums.DataType.*;

public class JT808MessageBaseDecoder {

    /**
     * 获取消息ID
     * @param source
     * @return
     */
    public int getType(ByteBuf source) {
        return source.getUnsignedShort(0);
    }

    /**
     * 去掉首尾的7e标识，如果没有标识，则认为消息不对，直接不处理了
     * @param in
     * @return
     */
    public ByteBuf checkAndRemove7E(ByteBuf in) {
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

    /**
     * 将正文转换成消息体bean
     * @param in
     * @param handler
     * @return
     */
    public AbstractMessage<? extends AbstractBody> decodeIn2Message(ByteBuf in, Handler handler) {
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
        return decode;
    }

    /**
     * 解析
     */
    private <T extends AbstractBody> AbstractMessage<T> decode(ByteBuf buf, Class<? extends AbstractMessage> clazz, Class<T> bodyClass) {
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

    private <T> T decode(ByteBuf buf, Class<T> targetClass) {
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

    private Object read(ByteBuf buf, Property prop, int length, PropertyDescriptor pd) {
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

    /**
     * 校验
     */
    private boolean check(ByteBuf buf) {
        byte checkCode = buf.getByte(buf.readableBytes() - 1);
        buf = buf.slice(0, buf.readableBytes() - 1);
        byte calculatedCheckCode = ByteBufUtils.bcc(buf);

        return checkCode != calculatedCheckCode;
    }

    /**
     * 反转义
     */
    private ByteBuf unEscape(ByteBuf source) {
        int low = source.readerIndex();
        int high = source.writerIndex();

        int mark = source.indexOf(low, high, (byte) 0x7d);
        if (mark == -1)
            return source;

        List<ByteBuf> bufList = new ArrayList<>(3);

        int len;
        do {

            len = mark + 2 - low;
            bufList.add(slice(source, low, len));
            low += len;

            mark = source.indexOf(low, high, (byte) 0x7d);
        } while (mark > 0);

        bufList.add(source.slice(low, high - low));

        return new CompositeByteBuf(UnpooledByteBufAllocator.DEFAULT, false, bufList.size(), bufList);
    }

    /**
     * 截取转义前报文，并还原转义位
     */
    private ByteBuf slice(ByteBuf byteBuf, int index, int length) {
        byte second = byteBuf.getByte(index + length - 1);
        if (second == 0x02)
            byteBuf.setByte(index + length - 2, 0x7e);

        // 0x01 不做处理 p47
        // if (second == 0x01) {
        // }
        return byteBuf.slice(index, length - 1);
    }
}
