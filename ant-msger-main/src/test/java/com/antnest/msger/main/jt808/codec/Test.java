package com.antnest.msger.main.jt808.codec;

import com.antnest.msger.core.config.Charsets;
import org.apache.commons.codec.binary.Hex;

public class Test {

    public static void main(String[] args) throws Exception {
//        System.out.println(Hex.encodeHexString("JS5-HD-105V".getBytes(Charsets.GBK)));
//        System.out.println(Hex.encodeHexString("T17051604-V705061".getBytes(Charsets.GBK)));
//
//        System.out.println(new String(Hex.decodeHex("073731363033"), Charsets.GBK));
        System.out.println(new String(Hex.decodeHex("3031333838383838383838385F617574686564"), Charsets.GBK));
//        System.out.println(new String(Hex.decodeHex("8401"), Charsets.GBK));
//
//        System.out.println(Bcd.encode8421String(Hex.decodeHex("3689860225131650397533")));
//        System.out.println(Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump("8401")).readUnsignedShort());
    }
}