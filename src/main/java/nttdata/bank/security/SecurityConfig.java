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

import static nttdata.bank.utils.ConstatesUtils.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SecurityFilterRequest securityFilterRequest;

    public SecurityConfig(SecurityFilterRequest securityFilterRequest) {
        this.securityFilterRequest = securityFilterRequest;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> {
                    req.requestMatchers(HttpMethod.POST, URI_AUTENTICACAO).permitAll();
                    req.requestMatchers(HttpMethod.POST, URI_USUARIO).permitAll();
                    req.requestMatchers(HttpMethod.POST, URI_CONTA).permitAll();
                    req.requestMatchers(URIS_SWAGGER).permitAll();
                    req.requestMatchers(HttpMethod.PUT, URI_CONTA).hasRole(ADMIN);
                    req.requestMatchers(HttpMethod.DELETE, URI_CONTA).hasRole(ADMIN);
                    req.requestMatchers(HttpMethod.PUT, URI_USUARIO).hasRole(ADMIN);
                    req.requestMatchers(HttpMethod.DELETE, URI_USUARIO).hasRole(ADMIN);
                    http.addFilterBefore(securityFilterRequest, UsernamePasswordAuthenticationFilter.class);
                    req.anyRequest().authenticated();
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
