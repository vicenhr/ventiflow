package mx.com.ventiflow.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig{
    private final JwtAuthFilter jwtAuthFilter;

    public  SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/pantalla-publica").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/pedidos").hasAnyRole("CAJA", "ADMIN")
                        .requestMatchers("/api/v1/clientes/**").hasAnyRole("CAJA", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/activos").hasAnyRole("COCINA", "BARRA", "CAJA", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/pedidos/{idPedido}/estado").hasAnyRole("COCINA", "BARRA", "CAJA", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/metricas").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/pedidos/{idPedido}/cancelar").hasRole("ADMIN")
                        .requestMatchers("/ws-ventiflow/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .cors(Customizer.withDefaults())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
