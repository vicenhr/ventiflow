package mx.com.ventiflow.infra.websocket;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import mx.com.ventiflow.infra.security.JwtService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {
    private static final String PREFIJO = "Bearer ";
    private static final String TOKEN_INVALIDO = "Token invalido";
    private static final String ACCESO_DENEGADO = "Acceso denegado";
    private static final String TOPIC_PUBLICO = "/topic/cds/publico";
    private static final String TOPIC_COCINA = "/topic/kds/cocina";
    private static final String TOPIC_BARRA = "/topic/kds/barra";

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            autenticar(accessor);
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            if (!puedeSuscribirse(accessor.getDestination(), accessor.getUser())) {
                throw new MessagingException(ACCESO_DENEGADO);
            }
        }

        return message;
    }

    private void autenticar(StompHeaderAccessor accessor) {
        String header = accessor.getFirstNativeHeader("Authorization");

        if (header == null) {
            return; // anonimo (pantalla publica)
        }
        if (!header.startsWith(PREFIJO)) {
            throw new MessagingException(TOKEN_INVALIDO);
        }

        try {
            Claims claims = jwtService.leerClaims(header.substring(PREFIJO.length()));
            String rol = claims.get("rol", String.class);
            var autoridades = List.of(new SimpleGrantedAuthority("ROLE_" + rol));
            accessor.setUser(new UsernamePasswordAuthenticationToken(
                    claims.getSubject(), null, autoridades));
        } catch (JwtException e) {
            throw new MessagingException(TOKEN_INVALIDO);
        }
    }

    private boolean puedeSuscribirse(String destino, Principal usuario) {
        if (TOPIC_PUBLICO.equals(destino)) {
            return true;
        }
        if (!(usuario instanceof Authentication auth)) {
            return false; // anonimo en zona protegida
        }

        boolean esAdmin = tieneRol(auth, "ROLE_ADMIN");

        if (TOPIC_COCINA.equals(destino)) {
            return esAdmin || tieneRol(auth, "ROLE_COCINA");
        }
        if (TOPIC_BARRA.equals(destino)) {
            return esAdmin || tieneRol(auth, "ROLE_BARRA");
        }
        return false; // denegar por defecto
    }

    private boolean tieneRol(Authentication auth, String rol) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(rol));
    }
}