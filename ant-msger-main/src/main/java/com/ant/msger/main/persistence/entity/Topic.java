package com.ant.msger.main.persistence.entity;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class Topic {
    @JSONField
    private Integer id;

    @JSONField
    private String topicId;

    @JSONField
    private String appKey;

    @JSONField
    private String topicName;

    @JSONField
    private String bizId;

    @JSONField
    private String bizType;

    @JSONField(name="type")
    private Integer timeType;

    @JSONField(name="expiresTime",format = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    public Topic(String topicId) {
        this.topicId = topicId;
    }

    public Topic(String topicId, String appKey, String topicName, String bizId, String bizType, Integer timeType, Date expireTime) {
        this.topicId = topicId;
        this.appKey = appKey;
        this.topicName = topicName;
        this.bizId = bizId;
        this.bizType = bizType;
        this.timeType = timeType;
        this.expireTime = expireTime;
    }

    public Topic(Integer id, String topicId, String appKey, String topicName, String bizId, String bizType, Integer timeType, Date expireTime) {
        this.id = id;
        this.topicId = topicId;
        this.appKey = appKey;
        this.topicName = topicName;
        this.bizId = bizId;
        this.bizType = bizType;
        this.timeType = timeType;
        this.expireTime = expireTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        // 和终端手机号/userAlias位数保持一致
        if (StringUtils.isNotEmpty(topicId)) {
            int length = topicId.length();
            int lackLength = 20 - length;
            for (int i = 0; i < lackLength; i++) {
                topicId = "0".concat(topicId);
            }
        }
        this.topicId = topicId == null ? null : topicId.trim();
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey == null ? null : appKey.trim();
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName == null ? null : topicName.trim();
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId == null ? null : bizId.trim();
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType == null ? null : bizType.trim();
    }

    public Integer getTimeType() {
        return timeType;
    }

    public void setTimeType(Integer timeType) {
        this.timeType = timeType;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}