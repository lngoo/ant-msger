package com.ant.msger.base.dto.persistence;

import java.util.Date;

public class TopicUser extends PersistenceObject {
    private String topicId;
    private String userId;
    private Date expireTime;

    public TopicUser(String topicId, String userId, Date expireTime) {
        this.topicId = topicId;
        this.userId = userId;
        this.expireTime = expireTime;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}
