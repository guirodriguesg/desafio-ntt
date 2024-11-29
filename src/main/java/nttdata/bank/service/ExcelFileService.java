package nttdata.bank.service;

import org.springframework.stereotype.Service;

@Service
public class ExcelFileService {


    public boolean isExcelFile(String fileName) {
        return fileName.endsWith(".xls") || fileName.endsWith(".xlsx");
    }

    public boolean isCsvFile(String fileName) {
        return fileName.endsWith(".csv");
    }
}
