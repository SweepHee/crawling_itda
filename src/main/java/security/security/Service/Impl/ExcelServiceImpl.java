package security.security.Service.Impl;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import security.security.Mapper.ExcelMapper;
import security.security.Mapper.SiSelectionsMapper;
import security.security.Service.ExcelService;
import security.security.Service.SiSelectionsService;
import security.security.Vo.SiSelectionsVO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    ExcelMapper excelMapper;

    @Autowired
    SiSelectionsService siSelectionsService;

    @Override
    public void uploadExcelFile(MultipartFile excelFile) {

    }

    @Override
    public void uploadSiSelectionsList(MultipartFile file) throws IOException {

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        if(!extension.equals("xlsx") && !extension.equals("xls")) {
            throw new IOException("엑셀파일만 업로드 해주세요.");
        }

        Workbook workbook = null;

        if (extension.equals("xlsx")) {
            workbook = new XSSFWorkbook(file.getInputStream());
        } else if (extension.equals("xls")) {
            workbook = new HSSFWorkbook(file.getInputStream());
        }

        Sheet worksheet = workbook.getSheetAt(2);

        List<SiSelectionsVO> siSelectionsVOList = new ArrayList<>();

        String group = null, name = null, description = null, etc = null;
        int price = 0;

        for (int i=2; i < worksheet.getPhysicalNumberOfRows(); i++) {

            Row row = worksheet.getRow(i);
            SiSelectionsVO vo = new SiSelectionsVO();

            /* 1셀이 공백이 아니면 이전 1셀이 담긴 group변수를 사용 */
            if (!Objects.equals(row.getCell(0).toString(), "")) {
                group = String.valueOf(row.getCell(0));
            }

            /* null 에러때문에 아래처럼 처리 */
            Cell cell1 = row.getCell(1);
            Cell cell2 = row.getCell(2);
            Cell cell3 = row.getCell(3);
            Cell cell4 = row.getCell(4);

            if (cell1 != null) {
                name = cell1.getStringCellValue();
            }

            if (cell2 != null) {
                description = cell2.getStringCellValue();
            }

            if (cell3 != null) {
                price = (int) cell3.getNumericCellValue();
            }

            if (cell4 != null) {
                etc = cell4.getStringCellValue();
            }

            vo.setTag(group);
            vo.setName(name);
            vo.setDescription(description);
            vo.setPrice(price);
            vo.setEtc(etc);

            /* 이미 있는 항목이면 업데이트, 없으면 크레이트 */
            if (siSelectionsService.isName(vo)) {
                siSelectionsService.update(vo);
            } else {
                siSelectionsVOList.add(vo);
            }

        }

        if (!siSelectionsVOList.isEmpty()) {
            siSelectionsService.upload(siSelectionsVOList);
        }

    }


}