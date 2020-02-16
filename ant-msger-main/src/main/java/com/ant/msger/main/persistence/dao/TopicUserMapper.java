package com.ant.msger.main.persistence.dao;

import com.ant.msger.main.persistence.entity.TopicUser;

import java.util.List;

public interface TopicUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TopicUser record);

    int insertSelective(TopicUser record);

    TopicUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TopicUser record);

    int updateByPrimaryKey(TopicUser record);

    void insertBatch(List<TopicUser> listDBBean) throws Exception;

    void updateBatch(List<TopicUser> listDBBean) throws Exception;

    void deleteBatch(List<TopicUser> listDBBean) throws Exception;
}