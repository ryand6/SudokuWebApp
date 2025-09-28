package com.github.ryand6.sudokuweb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // get from application.properties
    private final String spaBaseUrl;

    public WebSocketConfig(@Value("${spa.base-url}") String spaBaseUrl) {
        this.spaBaseUrl = spaBaseUrl;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefix for topics that clients can subscribe to
        config.enableSimpleBroker("/topic", "/queue");
        // Prefix for destinations clients can send messages to
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(spaBaseUrl)
                // fallback for old browsers/firewall configs that don't support/allow websockets
                .withSockJS()
                .setInterceptors(new HttpSessionHandshakeInterceptor());;
    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptor() {
//            @Override
//            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//                System.out.println("\uD83D\uDD25 INBOUND: headers=" + message.getHeaders());
//                return message;
//            }
//        });
//    }

}
