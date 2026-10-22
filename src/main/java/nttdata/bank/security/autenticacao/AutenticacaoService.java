package nttdata.bank.security.autenticacao;

import nttdata.bank.domain.entities.usuario.Usuario;
import nttdata.bank.repository.usuario.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AutenticacaoService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(AutenticacaoService.class);

    private final UsuarioRepository usuarioRepository;

    public AutenticacaoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Iniciando autenticacao do usuario");
        Optional<Usuario> usuario = usuarioRepository.findByLogin(username);
        if (usuario.isPresent()) {
            return usuario.get();
        }
        log.warn("Usuario nao encontrado");
        throw new UsernameNotFoundException("Usuario nao encontrado");
    }
}
