package nttdata.bank.controllers.autenticacao;

import nttdata.bank.controllers.autenticacao.requests.AutenticacaoRequest;
import nttdata.bank.controllers.autenticacao.responses.TokenResponse;
import nttdata.bank.domain.entities.usuario.Usuario;
import nttdata.bank.security.autenticacao.JwtTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/autenticacao")
public class AutenticacaoController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public AutenticacaoController(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping
    public ResponseEntity<?> autenticar(@RequestBody AutenticacaoRequest autenticacaoRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(autenticacaoRequest.login(), autenticacaoRequest.senha());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        if (authentication.isAuthenticated()) {

            String jwtToken = jwtTokenService.gerarToken((Usuario) authentication.getPrincipal());
            return ResponseEntity.ok(new TokenResponse(jwtToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
