package com.ant.msger.main.framework.log;

import com.ant.msger.base.message.AbstractMessage;
import com.ant.msger.main.framework.session.Session;
import org.slf4j.LoggerFactory;

public class Logger {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Logger.class.getSimpleName());

    public String logMessage(String type, AbstractMessage message, String hex) {
        String log = type + " " + hex;
        logger.info(log);
        return log;
    }

    public String logEvent(String event, Session session) {
        String log = event + " " + session.getUserAlias();
        logger.info(log);
        return log;
    }
}