package nttdata.bank.service.usuario;

import nttdata.bank.controllers.usuario.requests.UsuarioRequest;
import nttdata.bank.controllers.usuario.responses.UsuarioResponse;
import nttdata.bank.domain.dto.usuario.UsuarioDTO;
import nttdata.bank.domain.entities.usuario.Usuario;
import nttdata.bank.handlers.UsuarioException;
import nttdata.bank.mappers.usuario.UsuarioMapper;
import nttdata.bank.repository.usuario.UsuarioRepository;
import nttdata.bank.service.ExcelFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static nttdata.bank.utils.ConstatesUtils.*;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final ExcelFileService excelFileService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, ExcelFileService excelFileService, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.excelFileService = excelFileService;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<Usuario> getAllUsers(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    public Optional<Usuario> getUserById(Long id) {
        return usuarioRepository.findById(id);
    }

    public UsuarioResponse createUser(UsuarioRequest usuarioRequest) {
        Usuario usuario = toUsuarioEntity(usuarioRequest);
        usuario.setSenha(passwordEncoder.encode(usuarioRequest.senha()));
        try {
            return toUsuarioResponse(usuarioRepository.save(usuario));
        } catch (Exception e) {
            if (e.getMessage().contains("usuario_uk_login")) {
                log.error("Login ja cadastrado");
                throw new UsuarioException("Login ja cadastrado", BAD_REQUEST);
            }
            throw new UsuarioException("Erro ao criar cliente", INTERNAL_SERVER_ERROR);
        }
    }

    public UsuarioResponse updateUser(Long idUsuario, UsuarioRequest usuarioRequest) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(idUsuario);
        if (usuarioOptional.isEmpty()) {
            log.warn("Usuario com id {} nao encontrando", idUsuario);
            throw new UsuarioException("Usuario nao encontrado", NOT_FOUND);
        }

        Usuario usuario = usuarioOptional.get();
        usuario.setNome(usuarioRequest.nome());
        usuario.setLogin(usuarioRequest.login());
        usuario.setEmail(usuarioRequest.email());
        usuario.setTipoUsuario(usuarioRequest.tipoUsuario());
        try {
            return toUsuarioResponse(usuarioRepository.save(usuario));
        } catch (Exception e) {
            if (e.getMessage().contains("usuario_uk_login")) {
                log.error("Login ja cadastrado");
                throw new UsuarioException("Login ja cadastrado", BAD_REQUEST);
            }
            throw new UsuarioException("Erro ao criar cliente", INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteUser(Long id) {
        log.info("Deleting user by id {}", id);
        usuarioRepository.deleteById(id);
    }

    public Optional<List<UsuarioResponse>> createUsersByExecel(MultipartFile file) {
        List<Usuario> usuarios;

        if (file.isEmpty()) {
            log.error("Arquivo vazio");
            throw new UsuarioException("Arquivo vazio", NOT_FOUND);
        }

        if (!excelFileService.isExcelFile(file.getOriginalFilename())) {
            log.error("Arquivo invalido");
            throw new UsuarioException("Arquivo invalido", BAD_REQUEST);
        }

        try {
            usuarios = toUsuarioDTO(excelFileService.importUsersFromExcel(file.getInputStream()));
            return Optional.of(usuarioRepository.saveAll(usuarios).stream().map(usuarioMapper::toUsuarioResponse).toList());
        } catch (Exception e) {
            log.error("Erro ao criar usuarios por excel", e);
            throw new UsuarioException("Erro ao criar usuarios por excel", INTERNAL_SERVER_ERROR);
        }
    }

    public Usuario toUsuarioEntity(UsuarioRequest usuarioRequest) {
        return usuarioMapper.toUsuarioEntity(usuarioRequest);
    }
    public UsuarioResponse toUsuarioResponse(Usuario usuario) {
        return usuarioMapper.toUsuarioResponse(usuario);
    }

    public List<Usuario> toUsuarioDTO(List<UsuarioDTO> usuarioDTOs) {
        return usuarioMapper.toUsuarios(usuarioDTOs);
    }

    //TODO: CRIAR METODO PARA ALTERAR SENHA
}
