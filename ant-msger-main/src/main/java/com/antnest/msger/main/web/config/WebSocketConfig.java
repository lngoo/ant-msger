package com.antnest.msger.main.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.antnest.msger.main.web.component.WebSocketInterceptor;
import com.antnest.msger.main.web.component.WebSocketMessageHandler;

@Configuration
public class WebSocketConfig implements WebSocketConfigurer {

    @Bean
    public WebSocketMessageHandler webSocketMessageHandler() {
        return new WebSocketMessageHandler();
    }

    @Bean
    public WebSocketInterceptor webSocketInterceptor() {
        return new WebSocketInterceptor();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        WebSocketMessageHandler webSocketHandler = webSocketMessageHandler();
        WebSocketInterceptor webSocketInterceptor = webSocketInterceptor();
        registry.addHandler(webSocketHandler, "/websocket").addInterceptors(webSocketInterceptor);
        registry.addHandler(webSocketHandler, "/sockjs").addInterceptors(webSocketInterceptor).withSockJS();
    }
}