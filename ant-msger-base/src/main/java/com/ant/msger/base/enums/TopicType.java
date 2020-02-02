package com.ant.msger.base.enums;

/**
 * topic类型
 *
 */
public enum TopicType {
    TO_DEVICE, // 单一设备
    TO_USER,   // 某一用户，涉及到多个设备
    TO_DIALOG;  // 某个会话，涉及到会话内所有用户的所有设备
}
