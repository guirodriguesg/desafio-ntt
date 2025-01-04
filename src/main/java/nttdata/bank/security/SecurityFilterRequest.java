package nttdata.bank.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nttdata.bank.domain.entities.usuario.Usuario;
import nttdata.bank.repository.usuario.UsuarioRepository;
import nttdata.bank.security.autenticacao.JwtTokenService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static nttdata.bank.utils.ConstatesUtils.HEADER_AUTHORIZATION;
import static nttdata.bank.utils.ConstatesUtils.URI_AUTENTICACAO;

@Component
public class SecurityFilterRequest extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(SecurityFilterRequest.class);

    private final JwtTokenService jwtTokenService;
    private final UsuarioRepository usuarioRepository;

    public SecurityFilterRequest(JwtTokenService jwtTokenService, UsuarioRepository usuarioRepository) {
        this.jwtTokenService = jwtTokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(isLoginRequest(request) || isSwaggerRequest(request)){
            filterChain.doFilter(request, response);
            return;
        }

        final String token = getTokenFromHeader(request);
        if(StringUtils.isNotBlank(token)){
            final String subjectFromToken = this.jwtTokenService.getSubjectToken(token);

            Usuario usuario = usuarioRepository.findByLogin(subjectFromToken)
                    .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

            var authenticationToken = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }

    private static boolean isLoginRequest(HttpServletRequest request) {
        return (request.getRequestURI().equals(URI_AUTENTICACAO) && request.getMethod().equals("POST"));
    }

    private static boolean isSwaggerRequest(HttpServletRequest request) {
        return (request.getRequestURI().contains("swagger") || request.getRequestURI().contains("v3/api-docs"));
    }

    private String getTokenFromHeader(HttpServletRequest request){
        final String authHeader = request.getHeader(HEADER_AUTHORIZATION);
        if(notWasTokenInHeaderAuthorization(authHeader)){
            log.error("Token nao encontrado");
            throw new RuntimeException("Token nao encontrado");
        }
        return getBearerToken(authHeader);
    }

    private static boolean notWasTokenInHeaderAuthorization(String authHeader) {
        return authHeader == null || !authHeader.startsWith("Bearer ");
    }

    private static String getBearerToken(String authHeader) {
        return authHeader.substring(7);
    }
}
