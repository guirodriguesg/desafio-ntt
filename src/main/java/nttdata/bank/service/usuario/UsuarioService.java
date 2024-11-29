package nttdata.bank.service.usuario;

import nttdata.bank.controllers.usuario.requests.UsuarioRequest;
import nttdata.bank.controllers.usuario.responses.UsuarioResponse;
import nttdata.bank.domain.entities.usuario.TipoUsuarioEnum;
import nttdata.bank.domain.entities.usuario.Usuario;
import nttdata.bank.mappers.usuario.UsuarioMapper;
import nttdata.bank.repository.usuario.UsuarioRepository;
import nttdata.bank.service.ExcelFileService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final ExcelFileService excelFileService;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, ExcelFileService excelFileService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.excelFileService = excelFileService;
    }

    public Optional<List<UsuarioResponse>> getAllUsers() {
        log.info("Getting all users");
        return Optional.of(usuarioRepository.findAll().stream().map(usuarioMapper::toUsuarioResponse).toList());
    }

    public Optional<Usuario> getUserById(Long id) {
        log.info("Getting user by id {}", id);
        return usuarioRepository.findById(id);
    }

    public UsuarioResponse createUser(UsuarioRequest usuarioRequest) {
        log.info("Creating user");
        return toUsuarioResponse(usuarioRepository.save(toUsuarioEntity(usuarioRequest)));
    }

    public UsuarioResponse updateUser(Long idUsuario, UsuarioRequest usuarioRequest) {
        log.info("Updating user");
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(idUsuario);
        if (usuarioOptional.isEmpty()) {
            log.warn("Usuario com id {} nao encontrando", idUsuario);
            return null;
        }

        Usuario usuario = usuarioOptional.get();
        usuario.setNome(usuarioRequest.getNome());
        usuario.setLogin(usuarioRequest.getLogin());
        usuario.setSenha(usuarioRequest.getSenha());
        usuario.setEmail(usuarioRequest.getEmail());
        usuario.setTipoUsuario(usuarioRequest.getTipoUsuario());

        return toUsuarioResponse(usuarioRepository.save(usuario));
    }

    public void deleteUser(Long id) {
        log.info("Deleting user by id {}", id);
        usuarioRepository.deleteById(id);
    }

    public Optional<List<UsuarioResponse>> createUsersByExecel(MultipartFile file) {
        log.info("Creating users by excel");
        List<Usuario> usuarios;

        if (file.isEmpty()) {
            log.error("Arquivo vazio");
            return Optional.empty();
        }

        if (excelFileService.isExcelFile(file.getName())) {
            log.error("Arquivo invalido");
            return Optional.empty();
        }

        try {
            usuarios = importUsersFromExcel(file.getInputStream());

            return Optional.of(usuarios.stream().map(usuarioMapper::toUsuarioResponse).toList());
//            return Optional.of(usuarioRepository.saveAll(usuarios).stream().map(usuarioMapper::toUsuarioResponse).toList());
        } catch (Exception e) {
            log.error("Erro ao criar usuarios por excel", e);
            return Optional.empty();
        }
    }

    //MIGRAR
    public List<Usuario> importUsersFromExcel(InputStream fileInputStream) {
        List<Usuario> usuarios = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(fileInputStream)) {
            Sheet sheet = workbook.getSheetAt(0); // Seleciona a primeira aba

            // Itera sobre as linhas da planilha
            for (Row row : sheet) {
                // Ignora a primeira linha, que pode ser o cabeçalho
                if (row.getRowNum() == 0) {
                    continue;
                }

                Usuario usuario = new Usuario();
                usuario.setNome(row.getCell(0).getStringCellValue()); // Nome na primeira coluna
                usuario.setLogin(row.getCell(1).getStringCellValue()); // Idade na segunda coluna
                usuario.setSenha(row.getCell(2).getStringCellValue()); // Email na terceira coluna
                usuario.setTipoUsuario(TipoUsuarioEnum.valueOf(row.getCell(3).getStringCellValue()));
                usuario.setEmail(row.getCell(4).getStringCellValue()); // Email na terceira coluna
                usuarios.add(usuario);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Em um sistema real, você deve tratar exceções de forma mais robusta
        }

        return usuarios;
    }



    //MIGRAR
    public Usuario toUsuarioEntity(UsuarioRequest usuarioRequest) {
        return usuarioMapper.toUsuarioEntity(usuarioRequest);
    }
    public UsuarioResponse toUsuarioResponse(Usuario usuario) {
        return usuarioMapper.toUsuarioResponse(usuario);
    }

}
