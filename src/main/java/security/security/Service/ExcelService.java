package security.security.Service;

import org.springframework.web.multipart.MultipartFile;

public interface ExcelService {

    public void uploadExcelFile(MultipartFile excelFile);

}
