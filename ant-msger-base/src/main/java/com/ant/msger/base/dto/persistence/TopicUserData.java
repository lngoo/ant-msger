package com.ant.msger.base.dto.persistence;

import java.util.Date;

public class TopicUserData extends PersistenceData {
    private String topicId;
    private String appKey; // 应用注册码
    private String topicName; // 主题名称
    private String bizId; // 业务ID
    private String bizType; // 业务类型
    private Integer type; // 永久，1临时
    private Date expiresTime; // 到期时间
    private String member; // 用户成员集合，用,分割

    public TopicUserData() {
    }

    public TopicUserData(String topicId, String appKey, String topicName, String bizId, String bizType, Integer type, Date expiresTime, String member) {
        this.topicId = topicId;
        this.appKey = appKey;
        this.topicName = topicName;
        this.bizId = bizId;
        this.bizType = bizType;
        this.type = type;
        this.expiresTime = expiresTime;
        this.member = member;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getExpiresTime() {
        return expiresTime;
    }

    public void setExpiresTime(Date expiresTime) {
        this.expiresTime = expiresTime;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }
}
