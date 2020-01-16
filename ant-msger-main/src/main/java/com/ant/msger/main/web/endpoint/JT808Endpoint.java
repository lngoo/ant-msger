package com.ant.msger.main.web.endpoint;

import com.ant.msger.base.annotation.Endpoint;
import com.ant.msger.base.annotation.Mapping;
import com.ant.msger.base.dto.jt808.*;
import com.ant.msger.base.dto.jt808.basics.Message;
import com.ant.msger.base.message.SyncFuture;
import com.ant.msger.main.framework.session.MessageManager;
import com.ant.msger.main.framework.session.Session;
import com.ant.msger.main.framework.session.SessionManager;
import com.ant.msger.main.mq.sender.RequestDataJT808Sender;
import com.ant.msger.main.web.config.SessionKey;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import static com.ant.msger.base.common.MessageId.*;


@Endpoint
@Component
public class JT808Endpoint {

    @Autowired
    RequestDataJT808Sender jt808Sender;

    private static final Logger logger = LoggerFactory.getLogger(JT808Endpoint.class.getSimpleName());

    private SessionManager sessionManager = SessionManager.getInstance();

    private MessageManager messageManager = MessageManager.INSTANCE;

    //TODO Test
    public Object send(String mobileNumber, String hexMessage) {

        if (!hexMessage.startsWith("7e"))
            hexMessage = "7e" + hexMessage + "7e";
        ByteBuf msg = Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(hexMessage));
        Session session = SessionManager.getInstance().getByMobileNumber(mobileNumber);


        session.getChannel().writeAndFlush(msg);

        String key = mobileNumber;
        SyncFuture receive = messageManager.receive(key);
        try {
            return receive.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            messageManager.remove(key);
            e.printStackTrace();
        }
        return null;
    }

    public Object send(Message message) {
        return send(message, true);
    }

    public Object send(Message message, boolean hasReplyFlowIdId) {
        String mobileNumber = message.getMobileNumber();
        Session session = sessionManager.getByMobileNumber(mobileNumber);
        message.setSerialNumber(session.currentFlowId());

        session.getChannel().writeAndFlush(message);

        String key = mobileNumber + (hasReplyFlowIdId ? message.getSerialNumber() : "");
        SyncFuture receive = messageManager.receive(key);
        try {
            return receive.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            messageManager.remove(key);
            e.printStackTrace();
        }
        return null;
    }


    @Mapping(types = 终端通用应答, desc = "终端通用应答")
    public void 终端通用应答(Message<CommonResult> message) {
        CommonResult body = message.getBody();
        String mobileNumber = message.getMobileNumber();
        Integer replyId = body.getReplyId();
        messageManager.put(mobileNumber + replyId, message);
    }

    @Mapping(types = 查询终端参数应答, desc = "查询终端参数应答")
    public void 查询终端参数应答(Message<ParameterSettingReply> message) {
        ParameterSettingReply body = message.getBody();
        String mobileNumber = message.getMobileNumber();
        Integer replyId = message.getSerialNumber();
        messageManager.put(mobileNumber + replyId, message);
    }

    @Mapping(types = 查询终端属性应答, desc = "查询终端属性应答")
    public void 查询终端属性应答(Message<TerminalAttributeReply> message) {
        TerminalAttributeReply body = message.getBody();
        String mobileNumber = message.getMobileNumber();
        messageManager.put(mobileNumber, message);
    }

    @Mapping(types = {位置信息查询应答, 车辆控制应答}, desc = "位置信息查询应答/车辆控制应答")
    public void 位置信息查询应答(Message<PositionReply> message) {
        PositionReply body = message.getBody();
        String mobileNumber = message.getMobileNumber();
        Integer replyId = message.getSerialNumber();
        messageManager.put(mobileNumber + replyId, message);
    }

    @Mapping(types = 终端RSA公钥, desc = "终端RSA公钥")
    public void 终端RSA公钥(Message<RSAPack> message) {
        RSAPack body = message.getBody();
        String mobileNumber = message.getMobileNumber();
        messageManager.put(mobileNumber, message);
    }

    @Mapping(types = 摄像头立即拍摄命令应答, desc = "摄像头立即拍摄命令应答")
    public void 摄像头立即拍摄命令应答(Message<CameraShotReply> message) {
        CameraShotReply body = message.getBody();
        String mobileNumber = message.getMobileNumber();
        Integer replyId = message.getSerialNumber();
        messageManager.put(mobileNumber + replyId, message);
    }

    @Mapping(types = 存储多媒体数据检索应答, desc = "存储多媒体数据检索应答")
    public void 存储多媒体数据检索应答(Message<MediaDataQueryReply> message, Session session) {
        MediaDataQueryReply body = message.getBody();
        String mobileNumber = message.getMobileNumber();
        Integer replyId = message.getSerialNumber();
        messageManager.put(mobileNumber + replyId, message);
    }
    //=============================================================

    @Mapping(types = 终端心跳, desc = "终端心跳")
    public Message heartBeat(Message message, Session session) {
        CommonResult result = new CommonResult(终端心跳, message.getSerialNumber(), CommonResult.Success);
        return new Message(平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = 补传分包请求, desc = "补传分包请求")
    public Message 补传分包请求(Message<RepairPackRequest> message, Session session) {
        RepairPackRequest body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(补传分包请求, message.getSerialNumber(), CommonResult.Success);
        return new Message(平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = 终端注册, desc = "终端注册")
    public Message<RegisterResult> register(Message<Register> message, Session session) {
//        Register body = message.getBody();
        //TODO
//        if (session == null) {
//            session = initSession(message, socketAddress);
//        }
        sessionManager.put(message.getMobileNumber(), session);

        // 发送到redis
        jt808Sender.send(message);
        return null;
//        RegisterResult result = new RegisterResult(message.getSerialNumber(), RegisterResult.Success, "test_token");
//        return new Message(终端注册应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

//    private Session initSession(Message<Register> message, InetSocketAddress socketAddress) {
//        Session session = new Session();
//        session.setTerminalId(message.getMobileNumber());
//        session.setId(message.getMobileNumber());
//        session.setSocketAddress(socketAddress);
//        session.setChannel(sessionManager.getBySessionId(SessionKey.UDP_GLOBAL_CHANNEL_KEY).getChannel());
//        session.setLastCommunicateTimeStamp(System.currentTimeMillis());
//        return session;
//    }

    @Mapping(types = 终端注销, desc = "终端注销")
    public Message 终端注销(Message message, Session session) {
        //TODO
        CommonResult result = new CommonResult(终端注销, message.getSerialNumber(), CommonResult.Success);
        return new Message(平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = 终端鉴权, desc = "终端鉴权")
    public Message authentication(Message<Authentication> message, Session session) {
//        Authentication body = message.getBody();
        //TODO
//        session.setTerminalId(message.getMobileNumber());
//        sessionManager.put(Session.buildId(session.getChannel()), session);
//        // UDP
//        if (null == session.getId()) {
//            session = reCreateSession(message, socketAddress);
//        }
        session.setTerminalId(message.getMobileNumber());
        sessionManager.put(message.getMobileNumber(), session);

        // 发送到redis
        jt808Sender.send(message);
        return null;
//        CommonResult result = new CommonResult(终端鉴权, message.getSerialNumber(), CommonResult.Success);
//        return new Message(平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

//    private Session reCreateSession(Message<Authentication> message, InetSocketAddress socketAddress) {
//        Session session = new Session();
//        session.setId(message.getMobileNumber());
//        session.setSocketAddress(socketAddress);
//        session.setChannel(sessionManager.getBySessionId(SessionKey.UDP_GLOBAL_CHANNEL_KEY).getChannel());
//        session.setLastCommunicateTimeStamp(System.currentTimeMillis());
//        return session;
//    }

    @Mapping(types = 终端升级结果通知, desc = "终端升级结果通知")
    public Message 终端升级结果通知(Message<TerminalUpgradeNotify> message, Session session) {
        TerminalUpgradeNotify body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(终端升级结果通知, message.getSerialNumber(), CommonResult.Success);
        return new Message(平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = 位置信息汇报, desc = "位置信息汇报")
    public Message 位置信息汇报(Message<PositionReport> message, Session session) {
        PositionReport body = message.getBody();
        //TODO
        // 发送到redis
        jt808Sender.send(message);
        return null;

//        CommonResult result = new CommonResult(位置信息汇报, message.getSerialNumber(), CommonResult.Success);
//        return new Message(平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = 人工确认报警消息, desc = "人工确认报警消息")
    public Message 人工确认报警消息(Message<WarningMessage> message, Session session) {
        WarningMessage body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(位置信息汇报, message.getSerialNumber(), CommonResult.Success);
        return new Message(平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = 事件报告, desc = "事件报告")
    public Message 事件报告(Message<EventReport> message, Session session) {
        EventReport body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(事件报告, message.getSerialNumber(), CommonResult.Success);
        return new Message(平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = 提问应答, desc = "提问应答")
    public Message 提问应答(Message<QuestionMessageReply> message, Session session) {
        QuestionMessageReply body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(提问应答, message.getSerialNumber(), CommonResult.Success);
        return new Message(平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = 信息点播_取消, desc = "信息点播/取消")
    public Message 信息点播取消(Message<MessageSubOperate> message, Session session) {
        MessageSubOperate body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(信息点播_取消, message.getSerialNumber(), CommonResult.Success);
        return new Message(平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = 电话回拨, desc = "电话回拨")
    public Message 电话回拨(Message<CallPhone> message, Session session) {
        CallPhone body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(电话回拨, message.getSerialNumber(), CommonResult.Success);
        return new Message(平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = 行驶记录仪数据上传, desc = "行驶记录仪数据上传")
    public Message 行驶记录仪数据上传(Message message, Session session) {
        CommonResult result = new CommonResult(行驶记录仪数据上传, message.getSerialNumber(), CommonResult.Success);
        return new Message(平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = 行驶记录仪参数下传命令, desc = "行驶记录仪参数下达命令")
    public Message 行驶记录仪参数下传命令(Message message, Session session) {
        //TODO
        CommonResult result = new CommonResult(行驶记录仪参数下传命令, message.getSerialNumber(), CommonResult.Success);
        return new Message(平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = 电子运单上报, desc = "电子运单上报")
    public Message 电子运单上报(Message message, Session session) {
        //TODO
        CommonResult result = new CommonResult(电子运单上报, message.getSerialNumber(), CommonResult.Success);
        return new Message(平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = 驾驶员身份信息采集上报, desc = "驾驶员身份信息采集上报")
    public Message 驾驶员身份信息采集上报(Message<DriverIdentityInfo> message, Session session) {
        DriverIdentityInfo body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(驾驶员身份信息采集上报, message.getSerialNumber(), CommonResult.Success);
        return new Message(平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = 定位数据批量上传, desc = "定位数据批量上传")
    public Message 定位数据批量上传(Message<PositionReportBatch> message, Session session) {
        PositionReportBatch body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(定位数据批量上传, message.getSerialNumber(), CommonResult.Success);
        return new Message(平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = CAN总线数据上传, desc = "定位数据批量上传")
    public Message CAN总线数据上传(Message<CANBusReport> message, Session session) {
        CANBusReport body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(CAN总线数据上传, message.getSerialNumber(), CommonResult.Success);
        return new Message(平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = 多媒体事件信息上传, desc = "多媒体事件信息上传")
    public Message 多媒体事件信息上传(Message<MediaEventReport> message, Session session) {
        MediaEventReport body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(多媒体事件信息上传, message.getSerialNumber(), CommonResult.Success);
        return new Message(平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = 多媒体数据上传, desc = "多媒体数据上传")
    public Message 多媒体数据上传(Message<MediaDataReport> message, Session session) throws IOException {
        MediaDataReport body = message.getBody();

        byte[] packet = body.getPacket();
        FileOutputStream fos = new FileOutputStream("D://test.jpg");
        fos.write(packet);
        fos.close();

        MediaDataReportReply result = new MediaDataReportReply();
        result.setMediaId(body.getId());
        return new Message(平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = 数据上行透传, desc = "数据上行透传")
    public Message passthrough(Message<PassthroughPack> message, Session session) {
        PassthroughPack body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(数据上行透传, message.getSerialNumber(), CommonResult.Success);
        return new Message(平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = 数据压缩上报, desc = "数据压缩上报")
    public Message gzipPack(Message<GZIPPack> message, Session session) {
        GZIPPack body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(数据压缩上报, message.getSerialNumber(), CommonResult.Success);
        return new Message(平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

}