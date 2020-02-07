package com.ant.msger.main.mq.callable;

import com.ant.msger.base.enums.NotifyType;
import com.ant.msger.base.session.UserDevice;
import com.ant.msger.main.framework.session.UserManager;
import com.ant.msger.main.persistence.dao.UserDeviceMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * 用户设备更新器
 */
public class UserDeviceUpdator implements Callable<Boolean> {
    private static final Logger LOG = LoggerFactory.getLogger("responseChannel");

    private NotifyType notifyType;
    private List<UserDevice> lists;
    private UserDeviceMapper userDeviceMapper;

    public UserDeviceUpdator(NotifyType notifyType, List<UserDevice> lists, UserDeviceMapper userDeviceMapper) {
        this.notifyType = notifyType;
        this.lists = lists;
        this.userDeviceMapper = userDeviceMapper;
    }

    @Override
    public Boolean call() throws Exception {
        LOG.info("XXXXXXXXXXXXXXXXX");
//        switch (notifyType) {
//            case ADD:
//                insert(lists);
//                break;
//            case UPDATE:
//                update(lists);
//                break;
//            case DELETE:
//                delete(lists);
//                break;
//        }
        return true;
    }

    private void delete(List<UserDevice> userDevice) {
//        userDeviceMapper.deleteByDeviceId(userDevice.getDeviceId());
    }

    private void update(List<UserDevice> userDevice) {
        com.ant.msger.main.persistence.entity.UserDevice db = new com.ant.msger.main.persistence.entity.UserDevice();
        BeanUtils.copyProperties(userDevice, db);
        userDeviceMapper.updateByPrimaryKeySelective(db);
    }

    private void insert(List<UserDevice> userDevice) {
        com.ant.msger.main.persistence.entity.UserDevice db = new com.ant.msger.main.persistence.entity.UserDevice();
        BeanUtils.copyProperties(userDevice, db);
        userDeviceMapper.insertSelective(db);

        // 更新缓存

    }
//
//    private boolean checkParas() {
//        if (null == userDevice
//                || StringUtils.isEmpty(userDevice.getDeviceId())
//                || StringUtils.isEmpty(userDevice.getUserId())) {
//            return false;
//        } else {
//            return true;
//        }
//    }
}
