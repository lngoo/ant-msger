package com.ant.msger.main.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.ant.msger.base.enums.OperateType;
import com.ant.msger.main.persistence.callable.TopicUserDataUpdator;
import com.ant.msger.main.persistence.dao.TopicMapper;
import com.ant.msger.main.persistence.dao.TopicUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API接口controller
 * 全局错误码：
 * -1： 发生异常
 * -2： 参数不正确
 */
@RestController
@RequestMapping("/antMsger/api")
public class MsgerApiRestController {

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private TopicUserMapper topicUserMapper;

    /**
     * @param obj
     * @return
     */
    @PostMapping("/push/addTopic")
    public String addTopic(@RequestBody JSONObject obj) {
        try {
            TopicUserDataUpdator updator = new TopicUserDataUpdator(OperateType.TOPIC_REGISTER, obj, topicUserMapper, topicMapper);
            return updator.call();
        } catch (Exception e) {
            // Ignore
            return "-1";
        }
    }

    @PostMapping("/push/delTopic")
    public int delTopic(@RequestBody JSONObject obj) {
        try {
            TopicUserDataUpdator updator = new TopicUserDataUpdator(OperateType.TOPIC_RELEASE, obj, topicUserMapper, topicMapper);
            updator.call();
            return 0;
        } catch (Exception e) {
            // Ignore
            return -1;
        }
    }

    @PostMapping("/push/changeTopic")
    public int changeTopic(@RequestBody JSONObject obj) {
        try {
            TopicUserDataUpdator updator = new TopicUserDataUpdator(OperateType.TOPIC_UPDATE_TYPE, obj, topicUserMapper, topicMapper);
            updator.call();
            return 0;
        } catch (Exception e) {
            // Ignore
            return -1;
        }
    }

    @PostMapping("/push/topic/addMember")
    public int addMember(@RequestBody JSONObject obj) {
        try {
            TopicUserDataUpdator updator = new TopicUserDataUpdator(OperateType.TOPIC_ADDUSER, obj, topicUserMapper, topicMapper);
            updator.call();
            return 0;
        } catch (Exception e) {
            // Ignore
            return -1;
        }
    }

    @PostMapping("/push/topic/delMember")
    public int delMember(@RequestBody JSONObject obj) {
        try {
            TopicUserDataUpdator updator = new TopicUserDataUpdator(OperateType.TOPIC_REMOVEUSER, obj, topicUserMapper, topicMapper);
            updator.call();
            return 0;
        } catch (Exception e) {
            // Ignore
            return -1;
        }
    }
}
