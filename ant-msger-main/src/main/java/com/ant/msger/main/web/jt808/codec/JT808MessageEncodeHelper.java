package com.ant.msger.main.web.jt808.codec;

import com.ant.msger.base.dto.jt808.basics.Message;
import com.ant.msger.main.framework.commons.transform.HexUtil;
import io.netty.buffer.ByteBuf;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JT808MessageEncodeHelper {
    private static JT808MessageEncoder jT808MessageEncoder = new JT808MessageEncoder();

    public static String formatWebsocketMessage(Message message){
        ByteBuf byteBuf = jT808MessageEncoder.encode(message);
        byte[] bytes = byteBuf.array();
        int len = byteBuf.readableBytes();
        byte[] real = Arrays.copyOfRange(bytes, 0, len);
        String str = HexUtil.bytesToHexString(real);
        String delimiter = HexUtil.intTohex(message.getDelimiter());
        return delimiter.concat(str).concat(delimiter);
    }
}
