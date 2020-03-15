package com.antnest.msger.converter.mapping;

public interface HandlerMapper {

    Handler getHandler(Integer delimiter, Integer msgType);

}