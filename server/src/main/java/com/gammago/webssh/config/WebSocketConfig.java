package com.gammago.webssh.config;

import com.gammago.webssh.websocket.SshWebSocketHandler;
import com.gammago.webssh.websocket.SshWebSocketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket配置
 *
 * @author GammaGo
 * @date 2022/5/30
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private SshWebSocketHandler sshWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(sshWebSocketHandler, "/ws/ssh")
                .setAllowedOrigins("*")
                .addInterceptors(new SshWebSocketInterceptor());
    }
}
