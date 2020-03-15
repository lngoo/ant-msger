package com.antnest.msger.converter.endpoint;

import com.ant.msger.base.annotation.Endpoint;
import com.ant.msger.base.annotation.Mapping;
import com.ant.msger.base.dto.jt808.*;
import com.ant.msger.base.dto.jt808.basics.Message;
import com.antnest.msger.converter.constant.GlobalConfig;
import com.antnest.msger.converter.constant.ProtocolBusiness;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.ant.msger.base.common.MessageId.*;

@Component
@Endpoint
public class JT808Endpoint extends BaseEndpoint {

    @Mapping(types = 终端通用应答, desc = "终端通用应答")
    public void 终端通用应答(Message<CommonResult> message) {
    }

    @Mapping(types = 查询终端参数应答, desc = "查询终端参数应答")
    public void 查询终端参数应答(Message<ParameterSettingReply> message) {
    }

    @Mapping(types = 查询终端属性应答, desc = "查询终端属性应答")
    public void 查询终端属性应答(Message<TerminalAttributeReply> message) {
    }

    @Mapping(types = {位置信息查询应答, 车辆控制应答}, desc = "位置信息查询应答/车辆控制应答")
    public void 位置信息查询应答(Message<PositionReply> message) {
    }

    @Mapping(types = 终端RSA公钥, desc = "终端RSA公钥")
    public void 终端RSA公钥(Message<RSAPack> message) {
    }

    @Mapping(types = 摄像头立即拍摄命令应答, desc = "摄像头立即拍摄命令应答")
    public void 摄像头立即拍摄命令应答(Message<CameraShotReply> message) {
    }

    @Mapping(types = 存储多媒体数据检索应答, desc = "存储多媒体数据检索应答")
    public void 存储多媒体数据检索应答(Message<MediaDataQueryReply> message) {
    }

    @Mapping(types = 终端心跳, desc = "终端心跳")
    public void heartBeat(Message<TerminalHeartbeat> message) {
    }

    @Mapping(types = 补传分包请求, desc = "补传分包请求")
    public void 补传分包请求(Message<RepairPackRequest> message) {
    }

    @Mapping(types = 终端注册, desc = "终端注册")
    public void register(Message<Register> message) {
    }

    @Mapping(types = 终端注销, desc = "终端注销")
    public void 终端注销(Message message) {
    }

    @Mapping(types = 终端鉴权, desc = "终端鉴权")
    public void authentication(Message<Authentication> message) {
    }

    @Mapping(types = 终端升级结果通知, desc = "终端升级结果通知")
    public void 终端升级结果通知(Message<TerminalUpgradeNotify> message) {
    }

    @Mapping(types = 位置信息汇报, desc = "位置信息汇报")
    public void 位置信息汇报(Message<PositionReport> message) {
    }

    @Mapping(types = 人工确认报警消息, desc = "人工确认报警消息")
    public void 人工确认报警消息(Message<WarningMessage> message) {
    }

    @Mapping(types = 事件报告, desc = "事件报告")
    public void 事件报告(Message<EventReport> message) {
    }

    @Mapping(types = 提问应答, desc = "提问应答")
    public void 提问应答(Message<QuestionMessageReply> message) {
    }

    @Mapping(types = 信息点播_取消, desc = "信息点播/取消")
    public void 信息点播取消(Message<MessageSubOperate> message) {
    }

    @Mapping(types = 电话回拨, desc = "电话回拨")
    public void 电话回拨(Message<CallPhone> message) {
    }

    @Mapping(types = 行驶记录仪数据上传, desc = "行驶记录仪数据上传")
    public void 行驶记录仪数据上传(Message message) {
    }

    @Mapping(types = 行驶记录仪参数下传命令, desc = "行驶记录仪参数下达命令")
    public void 行驶记录仪参数下传命令(Message message) {
    }

    @Mapping(types = 电子运单上报, desc = "电子运单上报")
    public void 电子运单上报(Message message) {
    }

    @Mapping(types = 驾驶员身份信息采集上报, desc = "驾驶员身份信息采集上报")
    public void 驾驶员身份信息采集上报(Message<DriverIdentityInfo> message) {
    }

    @Mapping(types = 定位数据批量上传, desc = "定位数据批量上传")
    public void 定位数据批量上传(Message<PositionReportBatch> message) {
    }

    @Mapping(types = CAN总线数据上传, desc = "定位数据批量上传")
    public void CAN总线数据上传(Message<CANBusReport> message) {
    }

    @Mapping(types = 多媒体事件信息上传, desc = "多媒体事件信息上传")
    public void 多媒体事件信息上传(Message<MediaEventReport> message) {
    }

    @Mapping(types = 多媒体数据上传, desc = "多媒体数据上传")
    public void 多媒体数据上传(Message<MediaDataReport> message) throws IOException {
    }

    @Mapping(types = 数据上行透传, desc = "数据上行透传")
    public void passthrough(Message<PassthroughPack> message) {
    }

    @Mapping(types = 数据压缩上报, desc = "数据压缩上报")
    public void gzipPack(Message<GZIPPack> message) {
    }

    @Override
    public Integer getPointType() {
        return GlobalConfig.protocolBusinessMap().get(ProtocolBusiness.Jt808.name());
    }
}