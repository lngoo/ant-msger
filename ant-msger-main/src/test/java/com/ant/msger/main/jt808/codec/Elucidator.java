package com.ant.msger.main.jt808.codec;//package org.yzh.msger.codec;
//
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.ByteBufUtil;
//import io.netty.buffer.Unpooled;
//import org.yzh.framework.annotation.Property;
//import org.yzh.framework.message.AbstractBody;
//import org.yzh.web.msger.codec.JT808MessageDecoder;
//import org.yzh.web.msger.dto.PositionReport;
//import org.yzh.web.msger.dto.basics.Message;
//
//import java.beans.PropertyDescriptor;
//
///**
// * 阐释者，decode，将hex转换为bean
// *
// * @author zhihao.ye (yezhihaoo@gmail.com)
// */
//public class Elucidator extends JT808MessageDecoder {
//
//    private static final Elucidator elucidator = new Elucidator();
//
//    public static void main(String[] args) {
//        Class<? extends AbstractBody> clazz = PositionReport.class;
//        String hex = "0200002d010000000000007b000000070000000600000001000000020003000400051904061915541206000000000000110100e3040000000bfe";
//
//        System.out.println(hex);
//        System.out.println();
//        elucidator.decode(Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(hex)), Message.class, clazz);
//    }
//
//    @Override
//    public Object read(ByteBuf buf, Property prop, int length, PropertyDescriptor pd) {
//        buf.markReaderIndex();
//        String hex = ByteBufUtil.hexDump(buf.readSlice(length));
//        buf.resetReaderIndex();
//
//        Object value = super.read(buf, prop, length, pd);
//        System.out.println(prop.index() + "\t" + hex + "\t" + prop.desc() + "\t" + String.valueOf(value));
//        return value;
//    }
//}