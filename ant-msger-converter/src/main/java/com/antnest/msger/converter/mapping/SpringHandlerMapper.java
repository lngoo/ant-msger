package com.antnest.msger.converter.mapping;

import com.ant.msger.base.annotation.Mapping;
import com.antnest.msger.converter.endpoint.JT808Endpoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 特别注意：本包中缓存只有一层，和msger-main中的逻辑不一致，不一致，不一致
 */
public class SpringHandlerMapper implements HandlerMapper {

    private Map<Integer, Handler> handlerMap = new HashMap();

    public SpringHandlerMapper() {
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Handler getHandler(Integer key) {
        return handlerMap.get(key);
    }

    public void init() throws Exception {
        Class handlerClass = JT808Endpoint.class;
        Object entpointBean = handlerClass.newInstance();
        Method[] methods = handlerClass.getDeclaredMethods();
        if (methods != null) {
            for (Method method : methods) {
                if (method.isAnnotationPresent(Mapping.class)) {
                    Mapping annotation = method.getAnnotation(Mapping.class);
                    String desc = annotation.desc();
                    int[] types = annotation.types();
                    Handler value = new Handler(entpointBean, method, desc);
                    for (int type : types) {
                        handlerMap.put(type, value);
                    }
                }
            }
        }
    }
}