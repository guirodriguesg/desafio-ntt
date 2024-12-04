package nttdata.bank.security.autenticacao;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import nttdata.bank.domain.entities.usuario.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

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

    public boolean verificarToken(String token){
        DecodedJWT decodedJWT;
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);;
            JWTVerifier verifier = JWT.require(algorithm)
                    // specify any specific claim validations
                    .withIssuer("NTT BANK SERVICE")
                    // reusable verifier instance
                    .build();

            decodedJWT = verifier.verify(token);
        } catch (JWTVerificationException exception){
            throw new RuntimeException("Token inválido", exception);
        }
        return false;
    }

    private Instant getDataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
