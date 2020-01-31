package com.ant.msger.main.web.endpoint;

public abstract class BaseEndpoint {

    protected final Integer DELIMITER_JT808 = 0x7e;
    protected final Integer DELIMITER_IM = 0x7a;

    public abstract Integer getPointType();
}
