package com.antnest.msger.main.web.component;

import com.antnest.msger.core.message.AbstractMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.antnest.msger.main.framework.log.Logger;

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