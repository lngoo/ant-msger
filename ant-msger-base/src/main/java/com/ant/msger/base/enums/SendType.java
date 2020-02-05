package com.ant.msger.base.enums;

/**
 * 发送类型
 *
 */
public enum SendType {
    TO_SESSION, // 单一设备
    TO_USER,   // 某一用户，涉及到多个设备
    TO_TOPIC;  // 某个会话，涉及到会话内所有用户的所有设备
}
