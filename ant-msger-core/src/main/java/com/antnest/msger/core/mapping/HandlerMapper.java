package com.antnest.msger.core.mapping;

public interface HandlerMapper {

    Handler getHandler(Integer delimiter, Integer msgType);

}