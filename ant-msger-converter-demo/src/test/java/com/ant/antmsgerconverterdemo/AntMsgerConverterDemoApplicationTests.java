package com.ant.antmsgerconverterdemo;

import com.ant.msger.base.dto.jt808.Register;
import com.ant.msger.base.dto.jt808.basics.Message;
import com.ant.msger.base.message.AbstractMessage;
import com.antnest.msger.converter.Converter;
import com.antnest.msger.converter.util.JsonUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AntMsgerConverterDemoApplicationTests {

    @Autowired
    private Converter converter;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testConverter() {
        Message bean1 = register1();

        List<String> list = converter.beanToJt808Msg(bean1);
        System.out.println("############  hex 1 ###############");
        for (String hex1 : list) {
            System.out.println(hex1);
            AbstractMessage bean2 = converter.jt808MsgToBean("1", hex1);
            if (bean2 != null) {
                String json1 = JsonUtils.toJson(bean1);
                String json2 = JsonUtils.toJson(bean2);
                System.out.println("########### JSON ################");
                System.out.println(json1);
                System.out.println(json2);
                assertEquals("object not equals", json1, json2);

                System.out.println("############  hex 2 ###############");
                List<String> list2 = converter.beanToJt808Msg((Message)bean2);
                for (int i =0;i<list2.size();i++) {
                    String hex2 = list2.get(i);
                    System.out.println(hex2);
                    assertEquals("hex not equals", list.get(i), hex2);
                }
                return;
            }
        }
        assertEquals("没有正常拆分字符串", 1, 2);
    }

    private Message<Register> register1() {
        Register res = new Register();
        res.setProvinceId(2);
        res.setCityId(02);
        res.setManufacturerId("MAKE2");
        res.setTerminalType("PROBOOK zhan66-am2");
        res.setTerminalId("A2");
        res.setLicensePlateColor(02);
        res.setLicensePlate("川A77777");

        Message<Register> msg = new Message<>();
        msg.setType(0X0100);
        msg.setMobileNumber("12345678913888888888");
        msg.setSerialNumber(102);
        msg.setBody(res);
        return msg;
    }
}
