package mx.com.ventiflow.infra.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint al que se conectará el frontend (JavaScript)
        // allowedOriginPatterns evita errores de CORS cuando tu frontend está en otro puerto (ej. Live Server, React, Angular)
        registry.addEndpoint("/ws-ventiflow")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // Soporte para navegadores que no soportan WebSockets nativos
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefijo para los temas a los que el cliente se va a suscribir
        registry.enableSimpleBroker("/topic");
        // Prefijo para los mensajes que el cliente envía al servidor
        registry.setApplicationDestinationPrefixes("/app");
    }
}