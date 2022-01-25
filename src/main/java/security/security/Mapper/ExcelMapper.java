package security.security.Mapper;

import org.apache.ibatis.annotations.Mapper;
import security.security.Vo.SiSelectionsVO;

import java.util.List;

@Mapper
public interface ExcelMapper {

    public void uploadSiSelectionsList(List<SiSelectionsVO> vos);

}
