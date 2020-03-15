package com.antnest.msger.main.jt808.codec;//package org.yzh.msger.codec;
//
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.ByteBufUtil;
//import org.yzh.framework.annotation.Property;
//import org.yzh.web.msger.codec.JT808MessageEncoder;
//
///**
// * 逐暗者,encode,将bean转化为 16进制
// *
// * @author zhihao.ye (yezhihaoo@gmail.com)
// */
//public class DarkRepulsor extends JT808MessageEncoder {
//
//    private static final DarkRepulsor darkRepulsor = new DarkRepulsor();
//
//    public static void main(String[] args) {
////        ByteBuf byteBuf = darkRepulsor.encode(CoderTest.cameraShot());
//        ByteBuf byteBuf = darkRepulsor.encode(CoderTest.register1());
//        System.out.println();
//        System.out.println(ByteBufUtil.hexDump(byteBuf));
//    }
//
//    @Override
//    public void write(ByteBuf buf, Property prop, Object value) {
//        int before = buf.writerIndex();
//        super.write(buf, prop, value);
//        int after = buf.writerIndex();
//
//        String hex = ByteBufUtil.hexDump(buf, before, after - before);
//        System.out.println(prop.index() + "\t" + hex + "\t" + prop.desc() + "\t" + String.valueOf(value));
//    }
//}