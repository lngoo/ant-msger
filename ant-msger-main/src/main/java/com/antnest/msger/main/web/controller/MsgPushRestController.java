package com.antnest.msger.main.web.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.antnest.msger.core.common.MessageId;
import com.antnest.msger.core.dto.jt808.CommonResult;
import com.antnest.msger.core.dto.jt808.IMMsg;
import com.antnest.msger.core.dto.jt808.basics.Message;
import com.antnest.msger.main.framework.commons.enumeration.ProtocolCommunication;
import com.antnest.msger.main.framework.sender.ProtocolMsgSender;
import com.antnest.msger.main.framework.session.Session;
import com.antnest.msger.main.framework.session.SessionManager;
import com.antnest.msger.main.framework.session.TopicManager;
import com.antnest.msger.main.persistence.dao.TopicMapper;
import com.antnest.msger.main.persistence.dao.TopicUserMapper;
import com.antnest.msger.main.persistence.entity.TopicUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * 推送消息接口
 */
@RestController
@RequestMapping("/api")
public class MsgPushRestController {

    private ProtocolMsgSender protocolMsgSender = new ProtocolMsgSender();

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private TopicUserMapper topicUserMapper;

    /**
     * IM消息推送
     * target是目标user或topic
     * 1、	如果alias有值就是toUser
     * 2、	如果tag有值，就是toTopic
     * 3、	如果共存，处理成toUser
     * 4、	如果都没有，忽略
     *
     * 对应关系：
     *  Message               IMMsg
     *  fromUser              sendUserAlias
     * targets               sendTo
     * msgId        自己生成？
     * msgType               业务自定义类型，用于解析消息内容
     * msgBody               msg
     */
    @PostMapping("/push")
    public Result imPush(@RequestBody JSONObject obj) {
        try {
            JSONObject messagePushObj = obj.getJSONObject("message");
            if (null == messagePushObj) {
                return new Result(false, "message is null.");
            }

            JSONArray targets = messagePushObj.getJSONArray("targets");
            if (null == targets || targets.isEmpty()) {
                return new Result(false, "no targets data.");
            }

            for (int i = 0; i < targets.size(); i++) {
                JSONObject target = targets.getJSONObject(i);

                // 处理一个target
                doSendForOneTarget(target, messagePushObj);
            }
            return new Result(true, "success.");
        } catch (Exception e) {
            // Ignore
            return new Result(false, e.getMessage());
        }
    }

    private void doSendForOneTarget(JSONObject target, JSONObject messagePushObj) {
        String alias = target.getString("alias");
        if (StringUtils.isNotEmpty(alias)) {
            sendToUser(alias, messagePushObj);
        } else {
            String tag = target.getString("tag");
            if (StringUtils.isNotEmpty(tag)) {
                sendToTopic(tag, messagePushObj);
            }
        }
    }

    private void sendToTopic(String tag, JSONObject messagePushObj) {
        String fromUser = messagePushObj.getString("fromUser");
        String msgId = messagePushObj.getString("msgId");
        Integer msgType = messagePushObj.getInteger("msgType");
        String msgBody = messagePushObj.getString("msgBody");
        String createTime = DateFormatUtils.format(messagePushObj.getDate("createTime"), "yyyyMMddHHmmss");

        IMMsg imMsg = new IMMsg();
        imMsg.setMsg(msgBody);
        imMsg.setSendType(1);
        imMsg.setSendTo(tag);
        imMsg.setSendUserAlias(fromUser);
        imMsg.setSendTime(createTime);
        imMsg.setMsgId(msgId);
        imMsg.setMsgType(msgType);

        Message<IMMsg> msg = new Message<>();
        msg.setType(MessageId.IM消息);
        msg.setDelimiter(0x7a);
        msg.setMobileNumber("10000000000000000001");
        msg.setSerialNumber(1);
        msg.setBody(imMsg);

        Set<TopicUser> setTopicUsers = TopicManager.getInstance().getByTopicId(tag);
        if (null == setTopicUsers || setTopicUsers.isEmpty()) {
            return;
        }

        for (TopicUser topicUser : setTopicUsers) {
            Set<Session> sessions = SessionManager.getInstance().getByUserAlias(topicUser.getUserId());
            if (null == sessions || sessions.isEmpty()) {
                continue;
            }

            for (Session session : sessions) {
                sendMsgToSession(msg, session.getId());
            }
        }
    }

    /**
     * 对应关系：
     *  Message               IMMsg
     *  fromUser              sendUserAlias
     * targets               sendTo
     * msgId               msgId
     * msgType               msgType
     * msgBody               msg
     * createTime           sendTime
     */
    private void sendToUser(String alias, JSONObject messagePushObj) {
        String fromUser = messagePushObj.getString("fromUser");
        String msgId = messagePushObj.getString("msgId");
        Integer msgType = messagePushObj.getInteger("msgType");
        String msgBody = messagePushObj.getString("msgBody");
        String createTime = DateFormatUtils.format(messagePushObj.getDate("createTime"), "yyyyMMddHHmmss");

        IMMsg imMsg = new IMMsg();
        imMsg.setMsg(msgBody);
        imMsg.setSendType(2);
        imMsg.setSendTo(alias);
        imMsg.setSendUserAlias(fromUser);
        imMsg.setSendTime(createTime);
        imMsg.setMsgId(msgId);
        imMsg.setMsgType(msgType);

        Message<IMMsg> msg = new Message<>();
        msg.setType(MessageId.IM消息);
        msg.setDelimiter(0x7a);
        msg.setMobileNumber("10000000000000000001");
        msg.setSerialNumber(1);
        msg.setBody(imMsg);

        Set<Session> sessions = SessionManager.getInstance().getByUserAlias(alias);
        if (null == sessions || sessions.isEmpty()) {
            return;
        }

        for (Session session : sessions) {
            sendMsgToSession(msg, session.getId());
        }
    }

    private void sendMsgToSession(com.antnest.msger.core.dto.jt808.basics.Message message, String sessionId) {
        Session session = SessionManager.getInstance().getBySessionId(sessionId);
        if (message.getBody() instanceof CommonResult) {
            CommonResult commonResult = (CommonResult) message.getBody();
            if (commonResult.getReplyId() == MessageId.终端鉴权
                    && commonResult.getResultCode() == CommonResult.Success) {
                // 终端session鉴权成功，更新下链接时间
                session.setLastCommunicateTimeStamp(System.currentTimeMillis());
            }
        }

        message.setSerialNumber(session.currentFlowId());

        // 发送消息
        if (ProtocolCommunication.UDP == session.getProtocolCommunication()) {
            protocolMsgSender.sendUdpMsgSingle(session, message);
        } else if (ProtocolCommunication.WEBSOCKET == session.getProtocolCommunication()) {
            protocolMsgSender.sendWebsocketMsgSingle(session, message);
        } else {
            protocolMsgSender.sendTcpMsgSingle(session, message);
        }
    }

    class Result {
        private boolean sysResult;
        private String sysMsg;

        public Result(boolean sysResult, String sysMsg) {
            this.sysResult = sysResult;
            this.sysMsg = sysMsg;
        }

        public boolean isSysResult() {
            return sysResult;
        }

        public void setSysResult(boolean sysResult) {
            this.sysResult = sysResult;
        }

        public String getSysMsg() {
            return sysMsg;
        }

        public void setSysMsg(String sysMsg) {
            this.sysMsg = sysMsg;
        }
    }
}
