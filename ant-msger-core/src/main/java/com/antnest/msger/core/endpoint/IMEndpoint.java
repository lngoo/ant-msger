package com.antnest.msger.core.endpoint;

import com.antnest.msger.core.annotation.Endpoint;
import com.antnest.msger.core.annotation.Mapping;
import com.antnest.msger.core.constant.GlobalConfig;
import com.antnest.msger.core.constant.ProtocolBusiness;
import com.antnest.msger.core.dto.jt808.Authentication;
import com.antnest.msger.core.dto.jt808.CommonResult;
import com.antnest.msger.core.dto.jt808.IMMsg;
import com.antnest.msger.core.dto.jt808.Register;
import com.antnest.msger.core.dto.jt808.basics.Message;

import static com.antnest.msger.core.common.MessageId.*;

/**
 * 特别注意：在msger中应对本类的autoConfig做出替换
 */
@Endpoint
//@Component
public class IMEndpoint extends BaseIMEndpoint {


    @Mapping(types = 终端通用应答, desc = "终端通用应答")
    public void 终端通用应答(Message<CommonResult> message) {
    }
    //=============================================================

    @Mapping(types = 终端心跳, desc = "终端心跳")
    public void heartBeat(Message message) {
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

    @Mapping(types = IM消息, desc = "IM消息")
    public void imMsg(Message<IMMsg> message) {
    }

    @Override
    public Integer getPointType() {
        return GlobalConfig.protocolBusinessMap().get(ProtocolBusiness.AntIM.name());
    }
}