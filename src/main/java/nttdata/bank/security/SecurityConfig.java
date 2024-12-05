package nttdata.bank.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String URI_CONTA = "/api/v1/conta";
    private static final String URI_USUARIO = "/api/v1/usuario";
    private static final String URI_AUTENTICACAO = "/api/v1/conta";
    private static final String ADMIN = "ADMINISTRADOR";

    private final SecurityFilterRequest securityFilterRequest;

    public SecurityConfig(SecurityFilterRequest securityFilterRequest) {
        this.securityFilterRequest = securityFilterRequest;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> {
                    req.requestMatchers(URI_AUTENTICACAO).permitAll();
                    req.requestMatchers(HttpMethod.PUT, URI_CONTA).hasRole(ADMIN);
                    req.requestMatchers(HttpMethod.DELETE, URI_CONTA).hasRole(ADMIN);
                    req.requestMatchers(HttpMethod.PUT, URI_USUARIO).hasRole(ADMIN);
                    req.requestMatchers(HttpMethod.DELETE, URI_USUARIO).hasRole(ADMIN);
                    req.anyRequest().authenticated();
                    http.addFilterBefore(securityFilterRequest, UsernamePasswordAuthenticationFilter.class);
                }).build();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
