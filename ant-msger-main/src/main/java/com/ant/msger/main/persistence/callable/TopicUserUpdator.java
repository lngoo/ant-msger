package com.ant.msger.main.persistence.callable;

import com.ant.msger.base.enums.OperateType;
import com.ant.msger.base.dto.persistence.TopicUser;
import com.ant.msger.main.persistence.dao.TopicUserMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 会话用户更新器
 */
public class TopicUserUpdator implements Callable<Boolean> {

    private static final Logger LOG = LoggerFactory.getLogger("taskChannel");

    private OperateType operateType;
    private List<TopicUser> lists;
    private TopicUserMapper mapper;

    public TopicUserUpdator(OperateType operateType, List<TopicUser> lists, TopicUserMapper mapper) {
        this.operateType = operateType;
        this.lists = lists;
        this.mapper = mapper;
    }

    @Override
    public Boolean call() throws Exception {
        LOG.info("### start update topicUser data. one batch...");

        if (null == lists
                || lists.isEmpty()) {
            LOG.info("### no datas at this batch. stop all...");
            return true;
        }
        // 过滤掉不对的数据,获取到不正确数，同时将正确的转化为数据库对应的bean
        List<com.ant.msger.main.persistence.entity.TopicUser> listDBBean = new ArrayList<>();
        int failCount = filterNotRightData(listDBBean);
        LOG.info("### found {} dirty datas at this batch. right datas num = {}.", failCount, listDBBean.size());
        if (listDBBean.size() == 0) {
            LOG.warn("### no right datas. stop all...");
            return true;
        }

        try {
            switch (operateType) {
                case ADD:
                    insertBatch(listDBBean);
                    break;
                case UPDATE:
                    updateBatch(listDBBean);
                    break;
                case DELETE:
                    deleteBatch(listDBBean);
                    break;
            }
        } catch (Exception e) {
            LOG.warn("#### exception occurred when save db data...", e);
        }
        return true;
    }

    private void deleteBatch(List<com.ant.msger.main.persistence.entity.TopicUser> listDBBean) throws Exception {
        mapper.deleteBatch(listDBBean);
    }

    private void updateBatch(List<com.ant.msger.main.persistence.entity.TopicUser> listDBBean) throws Exception {
        mapper.updateBatch(listDBBean);
    }

    private void insertBatch(List<com.ant.msger.main.persistence.entity.TopicUser> listDBBean) throws Exception {
        mapper.insertBatch(listDBBean);
    }


    /**
     * 过滤不正确的数据，并返回不正确数
     * @return
     * @param listDBBean
     */
    private int filterNotRightData(List<com.ant.msger.main.persistence.entity.TopicUser> listDBBean) {
        Iterator<TopicUser> it = lists.iterator();
        AtomicInteger failCount = new AtomicInteger(0);
        while (it.hasNext()) {
            TopicUser temp = it.next();
            if (checkParas(temp)) {
                com.ant.msger.main.persistence.entity.TopicUser db = new com.ant.msger.main.persistence.entity.TopicUser();
                BeanUtils.copyProperties(temp, db);
                listDBBean.add(db);
            } else {
                failCount.addAndGet(1);
                lists.remove(temp);
                continue;
            }
        }
        return failCount.get();
    }

    private boolean checkParas(TopicUser topicUser) {
        if (null == topicUser
                || StringUtils.isEmpty(topicUser.getTopicId())
                || StringUtils.isEmpty(topicUser.getUserId())) {
            // 删除的时候，可以允许topicID不为空
            if (operateType == OperateType.DELETE
                    && StringUtils.isNotEmpty(topicUser.getTopicId())) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
}
