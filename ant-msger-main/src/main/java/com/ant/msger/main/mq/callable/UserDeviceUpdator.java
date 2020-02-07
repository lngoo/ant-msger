package com.ant.msger.main.mq.callable;

import com.ant.msger.base.enums.NotifyType;
import com.ant.msger.base.session.UserDevice;
import com.ant.msger.main.persistence.dao.UserDeviceMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

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
        LOG.info("### start update userDevice data. one batch...");

        if (null == lists
                || lists.isEmpty()) {
            LOG.info("### no datas at this batch. stop all...");
            return true;
        }
        // 过滤掉不对的数据,并获取到不正确数
        int failCount = filterNotRightData();
        LOG.warn("### found {} dirty datas at this batch. right datas num = {}.", failCount, lists.size() - failCount);

        switch (notifyType) {
            case ADD:
                insert(lists);
                break;
            case UPDATE:
                update(lists);
                break;
            case DELETE:
                delete(lists);
                break;
        }
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


    /**
     * 过滤不正确的数据，并返回不正确数
     * @return
     */
    private int filterNotRightData() {
        Iterator<UserDevice> it = lists.iterator();
        AtomicInteger failCount = new AtomicInteger(0);
        while (it.hasNext()) {
            UserDevice temp = it.next();
            if (checkParas(temp)) {
                failCount.addAndGet(1);
                lists.remove(temp);
                continue;
            }
        }
        return failCount.get();
    }

    private boolean checkParas(UserDevice userDevice) {
        if (null == userDevice
                || StringUtils.isEmpty(userDevice.getDeviceId())
                || StringUtils.isEmpty(userDevice.getUserId())) {
            return false;
        } else {
            return true;
        }
    }
}
