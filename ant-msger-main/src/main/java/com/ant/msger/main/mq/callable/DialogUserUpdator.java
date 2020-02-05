package com.ant.msger.main.mq.callable;

import com.ant.msger.base.enums.NotifyType;
import com.ant.msger.base.session.TopicUser;

import java.util.concurrent.Callable;

/**
 * 会话用户更新器
 */
public class DialogUserUpdator implements Callable<Boolean> {

    private NotifyType notifyType;
    private TopicUser topicUser;

    public DialogUserUpdator(NotifyType notifyType, TopicUser topicUser) {
        this.notifyType = notifyType;
        this.topicUser = topicUser;
    }

    @Override
    public Boolean call() throws Exception {
        // TODO
        return null;
    }
}
