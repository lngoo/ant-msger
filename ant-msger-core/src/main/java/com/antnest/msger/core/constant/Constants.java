package com.antnest.msger.core.constant;

/**
 * 常量类
 */
public class Constants {
    // 消息头长度
    public final static int JT808_MSG_HEADER_LENGTH = 21;
    // 消息体 body 长度 TODO
//    public final static int JT808_MSG_BODY_LENGTH = 1024; // 1024
    public final static int JT808_MSG_BODY_LENGTH = 30; // 测试分包时使用

    public class TopicType{
        public final static int FOREVER = 0; // 永久
        public final static int TEMPORARY = 1; // 临时
    }
}
