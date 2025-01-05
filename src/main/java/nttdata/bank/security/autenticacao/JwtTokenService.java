package nttdata.bank.security.autenticacao;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import nttdata.bank.domain.entities.usuario.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class JwtTokenService {

    @Value("${secret.jwt.token}")
    private String secret;

    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("NTT BANK SERVICE")
                    .withSubject(usuario.getLogin())
                    .withClaim("id", usuario.getId())
                    .withClaim("role", usuario.getTipoUsuario().name())
                    .withClaim("login", usuario.getLogin())
                    .withClaim("dataExpiracao", getDataExpiracao())
                    .sign(algoritmo);
        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao criar token JWT", exception);
        }
    }

    public String getSubjectToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return  JWT.require(algorithm)
                    .withIssuer("NTT BANK SERVICE")
                    .build().verify(token).getSubject();
        } catch (JWTVerificationException exception){
            throw new RuntimeException("Token inválido", exception);
        }
    }

    private Instant getDataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
