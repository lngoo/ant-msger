package com.ant.msger.main.framework.commons.constant;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GlobalConfig {

    private static Map<String, Integer> protocolBusinessMap = new HashMap<>();
    private static Integer[] delimiters;

    public GlobalConfig(Map<String, Integer> protocolBusinessMap) {
        this.protocolBusinessMap = protocolBusinessMap;
        delimiters = new Integer[protocolBusinessMap.values().size()];
        protocolBusinessMap.values().toArray(delimiters);
    }

    public static Map<String, Integer> protocolBusinessMap() {
        return protocolBusinessMap;
    }

    public static Integer[] delimiters() {
        return delimiters;
    }
}
