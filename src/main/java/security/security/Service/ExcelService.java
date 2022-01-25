package security.security.Service;

import org.springframework.web.multipart.MultipartFile;
import security.security.Vo.SiSelectionsVO;

import java.io.IOException;
import java.util.List;

public interface ExcelService {

    public void uploadExcelFile(MultipartFile excelFile);
    public void uploadSiSelectionsList(MultipartFile file) throws IOException;


}
