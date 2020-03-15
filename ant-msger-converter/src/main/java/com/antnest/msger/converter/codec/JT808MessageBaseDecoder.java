package com.antnest.msger.converter.codec;

import com.ant.msger.base.annotation.Property;
import com.ant.msger.base.dto.jt808.basics.Message;
import com.ant.msger.base.enums.DataType;
import com.ant.msger.base.message.AbstractBody;
import com.ant.msger.base.message.AbstractMessage;
import com.antnest.msger.converter.constant.GlobalConfig;
import com.antnest.msger.converter.mapping.Handler;
import com.antnest.msger.converter.mapping.HandlerMapper;
import com.antnest.msger.converter.redis.RedisFragMsgService;
import com.antnest.msger.converter.util.Bcd;
import com.antnest.msger.converter.util.BeanUtils;
import com.antnest.msger.converter.util.ByteBufUtils;
import com.antnest.msger.converter.util.PropertyUtils;
import io.netty.buffer.*;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.*;

import static com.ant.msger.base.enums.DataType.*;

public class JT808MessageBaseDecoder {

    public AbstractMessage<? extends AbstractBody> hexStringToBean(String clientSign, ByteBuf in, HandlerMapper handlerMapper, RedisFragMsgService fragMsgService) {
        // 获取标识符
        Integer delimiter = getOriInfoDelimiter(in);
        if (null == delimiter) {
            return null;
        }

        // 不是标准标识符，不解析报文
        if (!Arrays.asList(GlobalConfig.delimiters()).contains(delimiter)) {
            return null;
        }

        // 去掉首尾的标识，如果没有标识，则认为消息不对，直接不处理了
        in = in.slice(1, in.readableBytes() - 2);
        if (in == null) {
            return null;
        }

        int type = getType(in);
        Handler handler = handlerMapper.getHandler(delimiter, type);

        if (handler == null) {
            return null;
        }

        // 反转义及校验位检查
        in = unEscape(in);
        if (check(in)) {
            System.out.println("校验码错误" + ByteBufUtil.hexDump(in));
            return null;
        }

        // 尝试解码消息
        AbstractMessage<? extends AbstractBody> message = tryDecodeMessage(in, delimiter, handler, clientSign, fragMsgService);
        return message;
    }

    private AbstractMessage<? extends AbstractBody> tryDecodeMessage(ByteBuf in, Integer delimiter, Handler handler, String clientSign, RedisFragMsgService fragMsgService) {
        // 将输入转换为bean
        Message<? extends AbstractBody> message = decode(in, Message.class);
//        ByteBuf bodyByteBuf = readBodyBuf(in, message);
        ByteBuf bodyByteBuf = readBodyBuf(in, message);
        // 处理分包,分包时，拼接body
        if (message.isSubPackage()) {
            int totalFrag = message.getSubPackageTotal();
            int subPackageNumber = message.getSubPackageNumber();
            int serialNumber = message.getSerialNumber();
            Set existFragNums = fragMsgService.readMsgRedisFragNums(clientSign, serialNumber);
            // 分块全部齐了，拼装分块
            if (isFragEnough(existFragNums, subPackageNumber, totalFrag)) {
                // 获取redis中的缓存数据
                Map<Object, Object> map = fragMsgService.readMsgs(clientSign, serialNumber);
                // 分包拼装
                bodyByteBuf = combineFrags(bodyByteBuf.array(), subPackageNumber, map);
            } else {
                // 分块不齐，存redis后直接返回。保存body对应的bytes
                fragMsgService.saveMsgFrag(clientSign, serialNumber, subPackageNumber, bodyByteBuf.array());
                bodyByteBuf.skipBytes(bodyByteBuf.readableBytes());
                in.skipBytes(in.readableBytes());
                return null;
            }
        }

        // 转换body
        decodeBody(message, bodyByteBuf, handler);
//        AbstractMessage<? extends AbstractBody> message = decodeIn2Message(in, handler);
        message.setDelimiter(delimiter);
        bodyByteBuf.skipBytes(bodyByteBuf.readableBytes());
        in.skipBytes(in.readableBytes());
        return message;
    }

    /**
     * 分块是否凑齐了
     *
     * @param set
     * @param subPackageNumber
     * @param totalFrag
     * @return
     */
    private boolean isFragEnough(Set set, int subPackageNumber, int totalFrag) {
        set.add(subPackageNumber + "");
        return set.size() == totalFrag;
    }

    /*
     * 分包拼装
     */
    private ByteBuf combineFrags(byte[] array, int subPackageNumber, Map<Object, Object> map) {
        ByteBuf buf = Unpooled.buffer();
        map.put(subPackageNumber + "", array);
        for (int i = 0; i < map.size(); i++) {
            buf.writeBytes((byte[]) map.get(String.valueOf(i + 1)));
        }
        return buf.capacity(buf.readableBytes());
    }

    private <T extends AbstractBody> void decodeBody(Message<T> message, ByteBuf bodyByteBuf, Handler handler) {
        Type[] types = handler.getTargetParameterTypes();
        if (types[0] instanceof ParameterizedTypeImpl) {
            ParameterizedTypeImpl clazz = (ParameterizedTypeImpl) types[0];

            Class<T> bodyClass = (Class<T>) clazz.getActualTypeArguments()[0];

            T body = decode(bodyByteBuf, bodyClass);
            message.setBody(body);
        }
    }

    private ByteBuf readBodyBuf(ByteBuf in, Message<? extends AbstractBody> message) {
        Integer headerLength = message.getHeaderLength();
//        in.setIndex(headerLength, headerLength + message.getBodyLength());
//        ByteBuf byteBuf = Unpooled.buffer();
//        byteBuf.writeBytes();
//        byte[] bs = new byte[message.getBodyLength()];
        ByteBuf result = Unpooled.buffer(message.getBodyLength());
        result.writeBytes(in, headerLength, message.getBodyLength());
        return result;
    }

    private Integer getOriInfoDelimiter(ByteBuf in) {
        byte[] fristByte = ByteBufUtil.getBytes(in, 0, 1);
        byte[] endByte = ByteBufUtil.getBytes(in, in.readableBytes() - 1, 1);
        if (fristByte[0] == endByte[0]) {
            return Integer.valueOf(fristByte[0]);
        } else {
            return null;
        }
    }

    /**
     * 获取消息ID
     *
     * @param source
     * @return
     */
    public int getType(ByteBuf source) {
        return source.getUnsignedShort(0);
    }

//    /**
//     * 去掉首尾的7e标识，如果没有标识，则认为消息不对，直接不处理了
//     * @param in
//     * @return
//     */
//    public ByteBuf checkAndRemove7E(ByteBuf in) {
//        int stand = Integer.parseInt("7E", 16);
//        byte[] fristByte = ByteBufUtil.getBytes(in, 0, 1);
//        byte[] endByte = ByteBufUtil.getBytes(in, in.readableBytes() - 1, 1);
//        if (stand == fristByte[0]
//                && stand == endByte[0]) {
//            return in.slice(1, in.readableBytes() - 2);
//        } else {
//            return null;
//        }
//    }

    /**
     * 只转换message体，不转换body
     *
     * @param in
     * @param handler
     * @return
     */
    public Message<? extends AbstractBody> decodeIn2MessageOnlyHeader(ByteBuf in, Handler handler) {
        Message<? extends AbstractBody> decode = decode(in, Message.class);
        ;
        ;
        return decode;
    }

    /**
     * 将正文转换成消息体bean
     *
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
//        buf = unEscape(buf);
//
//        if (check(buf))
//            System.out.println("校验码错误" + ByteBufUtil.hexDump(buf));

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
