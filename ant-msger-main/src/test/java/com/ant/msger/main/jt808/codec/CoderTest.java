package com.ant.msger.main.jt808.codec;

import com.ant.msger.base.common.MessageId;
import com.ant.msger.base.dto.jt808.*;
import com.ant.msger.base.dto.jt808.basics.Message;
import com.ant.msger.base.message.AbstractBody;
import com.ant.msger.base.message.AbstractMessage;
import com.ant.msger.main.framework.commons.constant.GlobalConfig;
import com.ant.msger.main.framework.commons.transform.JsonUtils;
import com.ant.msger.main.framework.mapping.HandlerMapper;
import com.ant.msger.main.framework.codec.MsgSplitterEncoder;
import com.ant.msger.main.framework.redis.RedisFragMsgService;
import com.ant.msger.main.web.Application;
import com.ant.msger.main.web.jt808.codec.JT808MessageBaseDecoder;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * JT/T 808协议单元测试类
 *
 * @author zhihao.ye (yezhihaoo@gmail.com)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class CoderTest {

    //    private static final JT808MessageTcpDecoder decoder = new JT808MessageTcpDecoder();
//    private static final HandlerMapper handlerMapper = new SpringHandlerMapper("com.ant.msger.main.web.endpoint");
    @Autowired
    private HandlerMapper handlerMapper;

    @Autowired
    private RedisFragMsgService fragMsgService;

    private static final JT808MessageBaseDecoder decoder = new JT808MessageBaseDecoder();

    private static final MsgSplitterEncoder encoder = new MsgSplitterEncoder();

//    @Before
//    public void initHandler(){
//        System.out.println("init hanlder...");
//        ApplicationContext applicationContext = new
//
//    }

    public <T extends AbstractBody> AbstractMessage<T> transform(Class<T> clazz, String hex) {
        ByteBuf buf = Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(hex));
//        AbstractMessage<T> bean = decoder.decode(buf, Message.class, clazz);
        String clientSign = "test";

        AbstractMessage<T> bean = (AbstractMessage<T>) decoder.hexStringToBean(clientSign, buf, handlerMapper, fragMsgService);
        return bean;
    }

    public String transform(AbstractMessage bean) {
        ByteBuf buf = encoder.encode(bean);
        String hex = ByteBufUtil.hexDump(buf);
        return hex;
    }

    public List<String> transformWithSplit(AbstractMessage bean) {
        return encoder.splitAndEncode(bean);
    }

    public void selfCheck(Class<? extends AbstractBody> clazz, String hex1) {
        AbstractMessage bean1 = transform(clazz, hex1);

        String hex2 = transform(bean1);
        AbstractMessage bean2 = transform(clazz, hex2);

        String json1 = JsonUtils.toJson(bean1);
        String json2 = JsonUtils.toJson(bean2);
        System.out.println(hex1);
        System.out.println(hex2);
        System.out.println(json1);
        System.out.println(json2);
        System.out.println();

        assertEquals("hex not equals", hex1, hex2);
        assertEquals("object not equals", json1, json2);
    }

    public void selfCheck(AbstractMessage bean1) {
        List<String> list = transformWithSplit(bean1);
        System.out.println("############  hex 1 ###############");
        for (String hex1 : list) {
            System.out.println(hex1);
            AbstractMessage bean2 = transform(bean1.getBody().getClass(), hex1);
            if (bean2 != null) {
                String json1 = JsonUtils.toJson(bean1);
                String json2 = JsonUtils.toJson(bean2);
                System.out.println("########### JSON ################");
                System.out.println(json1);
                System.out.println(json2);
                assertEquals("object not equals", json1, json2);

                System.out.println("############  hex 2 ###############");
                List<String> list2 = transformWithSplit(bean2);
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

    public Message newMessage(AbstractBody body) {
        Message message = new Message();
        message.setType(125);
        message.setBodyProperties(1);
        message.setMobileNumber("018276468888");
        message.setSerialNumber(125);
        message.setEncryptionType(0);
        message.setReservedBit(0);
        message.setBody(body);
        return message;
    }


    // 位置信息汇报 0x0200
    @Test
    public void testPositionReport() {
//        String hex1 = "0200006a064762924976014d000003500004100201d9f1230743425e000300a6ffff190403133450000000250400070008000000e2403836373733323033383535333838392d627566322d323031392d30342d30332d31332d33342d34392d3735372d70686f6e652d2e6a706700000020000c14cde78d";
//        selfCheck(PositionReport.class, hex1);
        PositionReport report = new PositionReport();
        report.setDirection(1);
        report.setLatitude(1);
        report.setLongitude(2);
        report.setSpeed(1);
        report.setWarningMark(2);
        report.setStatus(2);
        report.setAltitude(1);

        Message<PositionReport> msg = new Message<>();
        msg.setType(0x0200);
        msg.setMobileNumber("12345678913888888888");
        msg.setSerialNumber(102);
        msg.setBody(report);

        selfCheck(msg);
    }


    // 终端注册应答 0x8100
    @Test
    public void testRegisterResult() {
        selfCheck(RegisterResult.class, "8001001601388888888800000065003031333838383838383838385f61757468656497");
    }

    // 终端鉴权 0x0102
    @Test
    public void testAuth() {
        Authentication authentication = new Authentication();
        authentication.setToken("12345678913888888888_authed");

        Message<Authentication> msg = new Message<>();
        msg.setType(0x0102);
        msg.setMobileNumber("12345678913888888888");
        msg.setSerialNumber(102);
        msg.setBody(authentication);

        selfCheck(msg);
    }


    // 终端注册 0x0100
//    @Test
//    public void testRegister() {
////        selfCheck(PositionReport.class, "0100002e064762924824000200000000484f4f5000bfb5b4ef562d31000000000000000000000000000000015a0d5dff02bba64450393939370002");
//        selfCheck(register());
//    }
//
//    public Message register() {
//        Register bean = new Register();
//        bean.setProvinceId(44);
//        bean.setCityId(307);
//        bean.setManufacturerId("测试");
//        bean.setTerminalType("TEST");
//        bean.setTerminalId("粤B8888");
//        bean.setLicensePlateColor(0);
//        bean.setLicensePlate("粤B8888");
//        return newMessage(bean);
//    }


    // 提问下发 0x8302
    @Test
    public void testQuestionMessage() {
        selfCheck(QuestionMessage.class, "8302001a017701840207001010062c2c2c2c2c2101000331323302000334353603000337383954");

        selfCheck(questionMessage());
    }

    public Message questionMessage() {
        QuestionMessage bean = new QuestionMessage();
        List<QuestionMessage.Option> options = new ArrayList();

        bean.buildSign(new int[]{1});
        bean.setContent("123");
        bean.setOptions(options);

        options.add(new QuestionMessage.Option(1, "asd1"));
        options.add(new QuestionMessage.Option(2, "zxc2"));
        return newMessage(bean);
    }


    // 设置电话本 0x8401
    @Test
    public void testPhoneBook() {
        selfCheck(PhoneBook.class, "0001002e02000000001500250203020b043138323137333431383032d5c5c8fd010604313233313233c0eecbc4030604313233313233cdf5cee535");

        selfCheck(phoneBook());
    }

    public Message phoneBook() {
        PhoneBook bean = new PhoneBook();
        bean.setType(PhoneBook.Append);
        bean.add(new PhoneBook.Item(2, "18217341802", "张三"));
        bean.add(new PhoneBook.Item(1, "123123", "李四"));
        bean.add(new PhoneBook.Item(3, "123123", "王五"));
        return newMessage(bean);
    }


    // 事件设置 0x8301
    @Test
    public void testEventSetting() {
        selfCheck(EventSetting.class, "83010010017701840207000c0202010574657374310205746573743268");

        selfCheck(eventSetting());
    }

    public Message eventSetting() {
        EventSetting bean = new EventSetting();
        bean.setType(EventSetting.Append);
        bean.addEvent(1, "test");
        bean.addEvent(2, "测试2");
        bean.addEvent(3, "t试2");
        return newMessage(bean);
    }

    // 终端&平台通用应答 0x0001 0x8001
    @Test
    public void testCommonResult() {
        selfCheck(CommonResult.class, "0001000501770184020701840038810300cd");
    }


    // 终端心跳 0x0002
    @Test
    public void testTerminalHeartbeat() {
//        selfCheck(TerminalHeartbeat.class, "00020000064762924976042fa7");
//        selfCheck(TerminalHeartbeat.class, "00020000013888888888042f10");
//        selfCheck(TerminalHeartbeat.class, "00020000013877778888042e11");

        selfCheck(heartBeat1());
    }


    // 文本信息下发 0x8300
    @Test
    public void testTextMessage() {
        selfCheck(TextMessage.class, "830000050647629242562692015445535480");
    }


    // 摄像头立即拍摄命令 0x8801
    @Test
    public void testCameraShot() {
        selfCheck(cameraShot());
        selfCheck(CameraShot.class, "8801000c0641629242524a43010001000a0001057d017d017d017d0125");
    }

    public Message<CameraShot> cameraShot() {
        CameraShot body = new CameraShot();
        body.setChannelId(125);
        body.setCommand(1);
        body.setParameter(125);
        body.setSaveSign(1);
        body.setResolution(125);
        body.setQuality(1);
        body.setBrightness(125);
        body.setContrast(1);
        body.setSaturation(125);
        body.setChroma(1);
        return newMessage(body);
    }

    public Message<Register> register1() {
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
//        msg.setMobileNumber("12345678913888889999");
        msg.setSerialNumber(102);
        msg.setBody(res);
        return msg;
    }

    public Message<TerminalHeartbeat> heartBeat1() {
        TerminalHeartbeat res = new TerminalHeartbeat();

        Message<TerminalHeartbeat> msg = new Message<>();
        msg.setType(0X0002);
        msg.setMobileNumber("12345678913888888888");
        msg.setSerialNumber(102);
        msg.setBody(res);
        return msg;
    }

    @Test
    public void testRegister1() {
//        selfCheck(Register.class, "0100002d0138888888880065000000014d414b4552202050524f424f4f4b207a68616e36362d616d640000000000413101b4a841383838383809");

        selfCheck(register1());
    }

    // 查询终端参数
    @Test
    public void terminalParameter() {
        ParameterSetting body = new ParameterSetting();
        body.setParameters(new ArrayList<>());
        Message<ParameterSetting> msg = new Message<>();
        msg.setType(MessageId.查询终端参数);
        msg.setMobileNumber("013888888888");
        msg.setSerialNumber(102);
        msg.setBody(body);
        selfCheck(msg);

        System.out.println(new XStream(new StaxDriver()).toXML(msg));
    }

    // IM 发送给用户
    @Test
    public void imSendToUser() {
        IMMsg imMsg = new IMMsg();
        imMsg.setMsg("你好啊！");
        imMsg.setSendType(2);
        imMsg.setSendTo("12345678913888888888");
        imMsg.setSendUserAlias("12345678913888889999");
        imMsg.setSendTime("20200314120000");

        Message<IMMsg> msg = new Message<>();
        msg.setType(MessageId.IM消息);
        msg.setDelimiter(0x7a);
        msg.setMobileNumber("12345678913888889999");
        msg.setSerialNumber(1);
        msg.setBody(imMsg);

        selfCheck(msg);
    }

    // IM 发送给用户
    @Test
    public void imSendToTopic() {
        IMMsg imMsg = new IMMsg();
        imMsg.setMsg("大家好！");
        imMsg.setSendType(1);
        imMsg.setSendTo("00398637937601941504");  // TODO 此处改改
        imMsg.setSendUserAlias("12345678913888889999");
        imMsg.setSendTime("20200314120000");
        imMsg.setMsgId("a123456789b987654321");

        Message<IMMsg> msg = new Message<>();
        msg.setType(MessageId.IM消息);
        msg.setDelimiter(0x7a);
        msg.setMobileNumber("12345678913888889999");
        msg.setSerialNumber(1);
        msg.setBody(imMsg);

        selfCheck(msg);
    }
}