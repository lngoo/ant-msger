package com.ant.msger.main.mq.callable;

import com.ant.msger.base.enums.NotifyType;
import com.ant.msger.base.session.DialogUser;
import com.ant.msger.base.session.UserDevice;

import java.util.concurrent.Callable;

/**
 * 会话用户更新器
 */
public class DialogUserUpdator implements Callable<Boolean> {

    private NotifyType notifyType;
    private DialogUser dialogUser;

    public DialogUserUpdator(NotifyType notifyType, DialogUser dialogUser) {
        this.notifyType = notifyType;
        this.dialogUser = dialogUser;
    }

    @Override
    public Boolean call() throws Exception {
        // TODO
        return null;
    }
}
