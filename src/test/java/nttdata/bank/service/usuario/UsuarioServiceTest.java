package nttdata.bank.service.usuario;

import nttdata.bank.controllers.usuario.requests.UsuarioRequest;
import nttdata.bank.controllers.usuario.responses.UsuarioResponse;
import nttdata.bank.domain.dto.usuario.UsuarioDTO;
import nttdata.bank.domain.entities.usuario.TipoUsuarioEnum;
import nttdata.bank.domain.entities.usuario.Usuario;
import nttdata.bank.mappers.usuario.UsuarioMapper;
import nttdata.bank.repository.usuario.UsuarioRepository;
import nttdata.bank.service.ExcelFileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private UsuarioMapper usuarioMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ExcelFileService excelFileService;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Deve retornar todos os usuarios")
    void getAllUsersReturnsAllUsers() {
        List<Usuario> usuarios = List.of(new Usuario());
        List<UsuarioResponse> usuarioResponses = List.of(new UsuarioResponse());

        when(usuarioRepository.findAll()).thenReturn(usuarios);
        when(usuarioMapper.toUsuarioResponse(any(Usuario.class))).thenReturn(usuarioResponses.get(0));

        Optional<List<UsuarioResponse>> result = usuarioService.getAllUsers();

        assertTrue(result.isPresent());
        assertEquals(usuarioResponses, result.get());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar usuario por id")
    void getUserById() {
        Long id = 1L;
        Usuario usuario = new Usuario();

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = usuarioService.getUserById(id);

        assertTrue(result.isPresent());
        assertEquals(usuario, result.get());
        verify(usuarioRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve retornar vazio quando usuario nao existe")
    void getUserByIdUserNotFound() {
        Long id = 1L;

        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Usuario> result = usuarioService.getUserById(id);

        assertFalse(result.isPresent());
        verify(usuarioRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve criar usuario")
    void createUserSuccess() {
        UsuarioRequest usuarioRequest = getUsuarioRequest();
        Usuario usuario = new Usuario();
        UsuarioResponse usuarioResponse = new UsuarioResponse();

        when(usuarioMapper.toUsuarioEntity(usuarioRequest)).thenReturn(usuario);
        when(passwordEncoder.encode(usuarioRequest.senha())).thenReturn("encodedPassword");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(usuarioMapper.toUsuarioResponse(usuario)).thenReturn(usuarioResponse);

        UsuarioResponse result = usuarioService.createUser(usuarioRequest);

        assertEquals(usuarioResponse, result);
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Deve atualizar usuario")
    void updateUserSuccess() {
        Long idUsuario = 1L;
        UsuarioRequest usuarioRequest = getUsuarioRequest();
        Usuario usuario = new Usuario();
        UsuarioResponse usuarioResponse = new UsuarioResponse();

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(usuarioMapper.toUsuarioResponse(usuario)).thenReturn(usuarioResponse);

        UsuarioResponse result = usuarioService.updateUser(idUsuario, usuarioRequest);

        assertEquals(usuarioResponse, result);
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Deve retornar nulo quando usuario nao existe")
    void updateUserUserNotFound() {
        Long idUsuario = 1L;
        UsuarioRequest usuarioRequest = getUsuarioRequest();

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.empty());

        UsuarioResponse result = usuarioService.updateUser(idUsuario, usuarioRequest);

        assertNull(result);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve deletar usuario por id")
    void deleteUserSuccess() {
        Long id = 1L;

        doNothing().when(usuarioRepository).deleteById(id);

        usuarioService.deleteUser(id);

        verify(usuarioRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Deve criar usuarios por excel")
    void createUsersByExcelSuccess() {
        MultipartFile file = mock(MultipartFile.class);
        List<Usuario> usuarios = List.of(new Usuario());
        List<UsuarioResponse> usuarioResponses = List.of(new UsuarioResponse());

        when(file.isEmpty()).thenReturn(false);
        when(excelFileService.isExcelFile(file.getName())).thenReturn(true);
        when(excelFileService.importUsersFromExcel(any())).thenReturn(List.of(new UsuarioDTO()));
        when(usuarioMapper.toUsuarios(anyList())).thenReturn(usuarios);
        when(usuarioRepository.saveAll(usuarios)).thenReturn(usuarios);
        when(usuarioMapper.toUsuarioResponse(any(Usuario.class))).thenReturn(usuarioResponses.getFirst());

        Optional<List<UsuarioResponse>> result = usuarioService.createUsersByExecel(file);
        assertTrue(result.isPresent());
        assertEquals(usuarioResponses, result.get());
        verify(usuarioRepository, times(1)).saveAll(usuarios);
    }

    @Test
    @DisplayName("Deve retornar vazio quando execel estiver vazio")
    void createUsersByExcelEmptyFile() {
        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(true);

        Optional<List<UsuarioResponse>> result = usuarioService.createUsersByExecel(file);

        assertFalse(result.isPresent());
        verify(usuarioRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Deve retornar vazio quando execel for invalido")
    void createUsersByExcelInvalidFile() {
        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(false);
        when(excelFileService.isExcelFile(file.getName())).thenReturn(false);

        Optional<List<UsuarioResponse>> result = usuarioService.createUsersByExecel(file);

        assertFalse(result.isPresent());
        verify(usuarioRepository, never()).saveAll(anyList());
    }

    private static UsuarioRequest getUsuarioRequest(){
        return new UsuarioRequest(1L, "nome", "login", "email", "senha", TipoUsuarioEnum.CLIENTE);
    }
}
