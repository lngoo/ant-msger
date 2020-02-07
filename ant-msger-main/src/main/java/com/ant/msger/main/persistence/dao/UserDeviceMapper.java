package com.ant.msger.main.persistence.dao;

import com.ant.msger.main.persistence.entity.UserDevice;

public interface UserDeviceMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByUserId();

    int insert(UserDevice record);

    int insertSelective(UserDevice record);

    UserDevice selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserDevice record);

    int updateByPrimaryKey(UserDevice record);
}