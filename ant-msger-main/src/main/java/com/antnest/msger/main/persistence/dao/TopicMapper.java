package com.antnest.msger.main.persistence.dao;

import com.antnest.msger.main.persistence.entity.Topic;

import java.util.List;

public interface TopicMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Topic record);

    int insertSelective(Topic record);

    Topic selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Topic record);

    int updateByPrimaryKey(Topic record);

    void deleteByTopicId(String topicId);

    List<Topic> loadAllEffective();

    void updateByTopicId(Topic topic);
}