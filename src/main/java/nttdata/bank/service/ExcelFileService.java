package nttdata.bank.service;

import io.micrometer.common.util.StringUtils;
import nttdata.bank.domain.dto.usuario.UsuarioDTO;
import nttdata.bank.domain.entities.usuario.TipoUsuarioEnum;
import nttdata.bank.handlers.ArquivoException;
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

import static nttdata.bank.utils.ConstatesUtils.*;

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

                if(StringUtils.isBlank(row.getCell(0).getStringCellValue())) {
                    continue;
                }
                UsuarioDTO usuario = new UsuarioDTO();
                usuario.setNome(row.getCell(0).getStringCellValue());
                usuario.setLogin(row.getCell(1).getStringCellValue());
                usuario.setSenha(row.getCell(2).getStringCellValue());
                usuario.setTipoUsuario(TipoUsuarioEnum.valueOf(row.getCell(3).getStringCellValue()));
                usuario.setEmail(row.getCell(4).getStringCellValue());
                usuarios.add(usuario);
            }
        } catch (Exception e) {
            log.error("Erro ao importar usuários do arquivo Excel", e);
            throw new ArquivoException("Erro ao importar usuários do arquivo Excel", INTERNAL_SERVER_ERROR);
        }

        return usuarios;
    }

    public boolean isExcelFile(String fileName) {
        return fileName.endsWith(MIME_XLS) || fileName.endsWith(MIME_XLSX);
    }

    public boolean isCsvFile(String fileName) {
        return fileName.endsWith(MIME_CSV);
    }
}
