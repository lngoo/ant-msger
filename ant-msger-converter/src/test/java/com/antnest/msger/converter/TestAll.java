//package com.antnest.msger.converter;
//
//import com.ant.msger.base.dto.jt808.Register;
//import com.ant.msger.base.dto.jt808.basics.Message;
//import com.antnest.msger.converter.util.HexUtil;
//import com.antnest.msger.converter.util.JsonUtils;
//import org.junit.Test;
//
//import static com.antnest.msger.converter.Converter.beanToJt808Msg;
//import static com.antnest.msger.converter.Converter.jt808MsgToBean;
//import static org.junit.Assert.assertEquals;
//
//public class TestAll {
//
//    @Test
//    public void testBeanToJt808() {
//        selfCheck(register1());
//    }
//
//    @Test
//    public void testJt808ToBean() {
//        selfCheck("7e0100405401123456789138888899990066000200022020202020204d414b453220202020202020202020202050524f424f4f4b207a68616e36362d616d3200000000000000000000000000000000000000000000000000000000413202b4a8413737373737927e");
//    }
//
//    public void selfCheck(String hex1) {
//        hex1 = hex1.toLowerCase();
//        Message bean1 = jt808MsgToBean(hex1);
//
//        String hex2 = beanToJt808Msg(bean1);
//        Message bean2 = jt808MsgToBean(hex2);
//
//        String json1 = JsonUtils.toJson(bean1);
//        String json2 = JsonUtils.toJson(bean2);
//        System.out.println(hex1);
//        System.out.println(hex2);
//        System.out.println(json1);
//        System.out.println(json2);
//        System.out.println();
//
//        assertEquals("hex not equals", hex1, hex2);
//        assertEquals("object not equals", json1, json2);
//    }
//
//    public void selfCheck(Message bean1) {
//        String hex1 = beanToJt808Msg(bean1);
//        System.out.println("直接转化的是：" + hex1);
//
//        Message bean2 = jt808MsgToBean(hex1);
//        String hex2 = beanToJt808Msg(bean2);
//        System.out.println("再次转化的是：" + hex2);
//
//        String json1 = JsonUtils.toJson(bean1);
//        String json2 = JsonUtils.toJson(bean2);
//
//        System.out.println(json1);
//        System.out.println(json2);
//        System.out.println();
//
//        assertEquals("hex not equals", hex1, hex2);
//        assertEquals("object not equals", json1, json2);
//    }
//
//    public Message<Register> register1() {
//        Register res = new Register();
//        res.setProvinceId(2);
//        res.setCityId(02);
//        res.setManufacturerId("MAKE2");
//        res.setTerminalType("PROBOOK zhan66-am2");
//        res.setTerminalId("A2");
//        res.setLicensePlateColor(02);
//        res.setLicensePlate("川A77777");
//
//        Message<Register> msg = new Message<>();
//        msg.setType(0X0100);
////        msg.setMobileNumber("12345678913888888888");
//        msg.setMobileNumber("12345678913888889999");
//        msg.setSerialNumber(102);
//        msg.setBody(res);
//
//        msg.setDelimiter((int) HexUtil.hexToByte("7a"));
//        return msg;
//    }
//}
