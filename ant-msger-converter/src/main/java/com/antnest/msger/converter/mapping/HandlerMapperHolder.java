package com.antnest.msger.converter.mapping;

public class HandlerMapperHolder {

    private static HandlerMapper handlerMapper;

    public static HandlerMapper getInstance() {
        if (null == handlerMapper) {
            synchronized (HandlerMapperHolder.class) {
                handlerMapper = new SpringHandlerMapper();
            }
        }
        return handlerMapper;
    }
}
