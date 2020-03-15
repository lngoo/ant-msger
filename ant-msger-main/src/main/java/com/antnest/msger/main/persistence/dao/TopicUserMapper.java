package com.antnest.msger.main.persistence.dao;

import com.antnest.msger.main.persistence.entity.TopicUser;

import java.util.List;

public interface TopicUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TopicUser record);

    int insertSelective(TopicUser record);

    TopicUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TopicUser record);

    int updateByPrimaryKey(TopicUser record);

    void insertBatch(List<TopicUser> list);

    void deleteByTopicId(String topicId);

    void deleteTopicUserBatch(List<TopicUser> list);

    List<TopicUser> loadAllEffective();
}