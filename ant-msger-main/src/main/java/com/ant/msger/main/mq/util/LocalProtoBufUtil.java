package com.ant.msger.main.mq.util;

import com.ant.msger.base.dto.jt808.*;
import com.ant.msger.base.message.AbstractBody;
import com.ant.msger.base.message.MessageExternal;
import com.ant.msger.main.framework.commons.transform.JsonUtils;
import com.antnest.msger.proto.ProtoMain;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;

import java.util.HashMap;
import java.util.Map;

import static com.ant.msger.main.mq.util.ProtoBufUtil.*;

/**
 * 本地化的转换类
 */
public class LocalProtoBufUtil {

    private static Map<String, Message.Builder> map = new HashMap<>();
    static {
        map.put(Register.class.getSimpleName(), ProtoMain.Register.newBuilder());
        map.put(CommonResult.class.getSimpleName(), ProtoMain.CommonResult.newBuilder());
        map.put(RegisterResult.class.getSimpleName(), ProtoMain.RegisterResult.newBuilder());
        map.put(Authentication.class.getSimpleName(), ProtoMain.Authentication.newBuilder());
        map.put(PositionReport.class.getSimpleName(), ProtoMain.PositionReport.newBuilder());
    }

    public static ProtoMain.Message copyMessageExternalToProtoBean(MessageExternal messageExternal, ProtoMain.Message.Builder target) {
        AbstractBody body = messageExternal.getMsgBody();
        messageExternal.setMsgBody(null);
        ProtoMain.Message msgProto = copyJavaBeanToProtoBean(messageExternal, target);

        Class bodyClass = body.getClass();
        Message.Builder bodyBuilder = map.get(bodyClass.getSimpleName());
        bodyBuilder.clear();
        Message bodyProto = copyJavaBeanToProtoBean(body, bodyBuilder);

        return msgProto.toBuilder().setMsgBody(Any.pack(bodyProto)).build();
    }

    public static MessageExternal copyProtoBeanToMessageExternal(ProtoMain.Message message) throws Exception {
        Any bodyAny = message.getMsgBody();
//        type.googleapis.com/RegisterResult
        String bodyClassName = bodyAny.getTypeUrl().replace("type.googleapis.com/", "");
        Message.Builder bodyBuilder = map.get(bodyClassName);
        Message bodyProto = bodyAny.unpack(bodyBuilder.build().getClass());
        Class bodyInternalClass = Class.forName("com.ant.msger.base.dto.jt808.".concat(bodyClassName));
        AbstractBody body = (AbstractBody) copyProtoBeanToJavaBean(bodyProto, bodyInternalClass);

        message = message.toBuilder().clearMsgBody().build();
        MessageExternal external = copyProtoBeanToJavaBean(message, MessageExternal.class);
        external.setMsgBody(body);

        return external;
    }
}
