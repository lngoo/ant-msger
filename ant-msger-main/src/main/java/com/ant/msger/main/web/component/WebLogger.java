package com.ant.msger.main.web.component;

import com.ant.msger.base.message.AbstractMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ant.msger.main.framework.log.Logger;

@Component
public class WebLogger extends Logger {

    @Autowired
    WebSocketMessageHandler webSocketMessageHandler;

    public String logMessage(String type, AbstractMessage message, String hex) {
        String log = super.logMessage(type, message, hex);
        webSocketMessageHandler.broadcast(log);
        return log;
    }
}