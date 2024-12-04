package nttdata.bank.service;

import nttdata.bank.domain.dto.usuario.UsuarioDTO;
import nttdata.bank.domain.entities.usuario.TipoUsuarioEnum;
import nttdata.bank.domain.entities.usuario.Usuario;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelFileService {

    private static final Logger log = LoggerFactory.getLogger(ExcelFileService.class);

    public List<UsuarioDTO> importUsersFromExcel(InputStream fileInputStream) {
        List<UsuarioDTO> usuarios = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(fileInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }

                UsuarioDTO usuario = new UsuarioDTO();
                usuario.setNome(row.getCell(0).getStringCellValue()); // Nome na primeira coluna
                usuario.setLogin(row.getCell(1).getStringCellValue()); // Idade na segunda coluna
                usuario.setSenha(row.getCell(2).getStringCellValue()); // Email na terceira coluna
                usuario.setTipoUsuario(TipoUsuarioEnum.valueOf(row.getCell(3).getStringCellValue()));
                usuario.setEmail(row.getCell(4).getStringCellValue()); // Email na terceira coluna
                usuarios.add(usuario);
            }
        } catch (Exception e) {
            log.error("Erro ao importar usuários do arquivo Excel", e);
        }

        return usuarios;
    }

    public boolean isExcelFile(String fileName) {
        return fileName.endsWith(".xls") || fileName.endsWith(".xlsx");
    }

    public boolean isCsvFile(String fileName) {
        return fileName.endsWith(".csv");
    }
}
