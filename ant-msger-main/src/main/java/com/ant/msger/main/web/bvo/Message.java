package com.ant.msger.main.web.bvo;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 老IM对象
 */
public class Message implements Serializable {
    private static final long serialVersionUID = -3317681956301081930L;

//    private static final MessageIdManager msgIdManager = new MessageIdManager();

    private String msgId;// 消息ID

    private String fromAppId; // 使用者的AppId

    private String fromUser;// 发起者别名或标签，可以是alias也可以是tag，也可以是alias@tag

    private String fromClientId;// 发起者当前的客户端ID

    private String toAppId; // 接收者的AppId

//    private List<Target> targets; // 接收者的别名、标签、客户端等信息，数量为空则不能发送

    private Integer msgType;// 类型

    private String msgBody;// 主要消息体

    private Map<String, String> extras;// 额外消息体

    private Integer feedBackTime; // 消息等待反馈期间持续发送的消息次数

    private long offlineExpireTime;// 离线缓存时间


    private String pushToPlatform; // 需要推送消息到的平台,android\ios\winphone\#ALL

    private Integer pushNetworkType; // 推送消息的网络类型,1：仅wifi下推送，0：不限制推送方式

    private Integer status;// 状态:0--未发送；1--已发送；2--已接收；3--已阅读

    private Date createTime;// 消息发起时间

    private Date sendTime;// 消息发出时间

    private Date reciveTime;// 消息接收时间

    private Date openTime;// 消息打开时间

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getFromAppId() {
        return fromAppId;
    }

    public void setFromAppId(String fromAppId) {
        this.fromAppId = fromAppId;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getFromClientId() {
        return fromClientId;
    }

    public void setFromClientId(String fromClientId) {
        this.fromClientId = fromClientId;
    }

    public String getToAppId() {
        return toAppId;
    }

    public void setToAppId(String toAppId) {
        this.toAppId = toAppId;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public Map<String, String> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, String> extras) {
        this.extras = extras;
    }

    public Integer getFeedBackTime() {
        return feedBackTime;
    }

    public void setFeedBackTime(Integer feedBackTime) {
        this.feedBackTime = feedBackTime;
    }

    public long getOfflineExpireTime() {
        return offlineExpireTime;
    }

    public void setOfflineExpireTime(long offlineExpireTime) {
        this.offlineExpireTime = offlineExpireTime;
    }

    public String getPushToPlatform() {
        return pushToPlatform;
    }

    public void setPushToPlatform(String pushToPlatform) {
        this.pushToPlatform = pushToPlatform;
    }

    public Integer getPushNetworkType() {
        return pushNetworkType;
    }

    public void setPushNetworkType(Integer pushNetworkType) {
        this.pushNetworkType = pushNetworkType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Date getReciveTime() {
        return reciveTime;
    }

    public void setReciveTime(Date reciveTime) {
        this.reciveTime = reciveTime;
    }

    public Date getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }
}
