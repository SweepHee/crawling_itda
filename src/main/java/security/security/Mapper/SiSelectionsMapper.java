package security.security.Mapper;

import org.apache.ibatis.annotations.Mapper;
import security.security.Vo.SiSelectionsVO;

import java.util.List;

@Mapper
public interface SiSelectionsMapper {

    public void upload(List<SiSelectionsVO> vos);
    public boolean isName(SiSelectionsVO vo);
    public void update(SiSelectionsVO vo);

}
