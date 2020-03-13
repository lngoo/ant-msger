package com.ant.msger.base.enums;

/**
 * 任务通道，通知类型
 */
public enum OperateType {
    TOPIC_REGISTER, // topic注册
    TOPIC_RELEASE, // topic解散
    TOPIC_UPDATE_TYPE, // topic变更是否永久属性
    TOPIC_ADDUSER, // 新增成员
    TOPIC_REMOVEUSER; // 删除成员
}
