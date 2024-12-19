package nttdata.bank.controllers.usuario;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import nttdata.bank.controllers.usuario.requests.UsuarioRequest;
import nttdata.bank.controllers.usuario.responses.UsuarioResponse;
import nttdata.bank.mappers.usuario.UsuarioMapper;
import nttdata.bank.service.usuario.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*") //ALTERAR PARA ORIGIN PERMITIDO
@RequestMapping("/api/v1/usuario")
@SecurityRequirement(name = "bearer-key")
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    public UsuarioController(UsuarioService usuarioService, UsuarioMapper usuarioMapper) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
    }

    @GetMapping
    private Optional<List<UsuarioResponse>> getAllUsers() {
        log.info("Buscando todos os usuarios");
        return usuarioService.getAllUsers();
    }

    @GetMapping("/{id}")
    private ResponseEntity<UsuarioResponse> getUserById(@PathVariable(value = "id") @NotNull Long idUsuario) {
        log.info("Buscando usuario com id: {}", idUsuario);
        return usuarioService.getUserById(idUsuario).map(usuarioMapper::toUsuarioResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<UsuarioResponse> createUser(@RequestBody @NotNull @Valid UsuarioRequest usuarioRequest) {
        log.info("Criando usuario");
        return ResponseEntity.ok(usuarioService.createUser(usuarioRequest));
    }

    @PutMapping("/{id}")
    private ResponseEntity<UsuarioResponse> updateUser(@PathVariable(value = "id") @NotNull Long idUsuario, @RequestBody @NotNull @Valid UsuarioRequest usuarioRequest) {
        log.info("Updating user");
        return ResponseEntity.ok(usuarioService.updateUser(idUsuario, usuarioRequest));
    }

    @DeleteMapping("/{id}")
    private ResponseEntity deleteUser(@PathVariable(value = "id") @NotNull Long id) {
        log.info("Deleting user by id {}", id);
        try {
            usuarioService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Erro ao deletar usuario", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/importa-usuario")
    public Optional<List<UsuarioResponse>> importarListaUsuarios(@RequestParam(name = "fileUsuarios") @NotNull MultipartFile file){
        return usuarioService.createUsersByExecel(file);
    }

}

//
//                .map(regimeTituloMapper::toResponse)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());