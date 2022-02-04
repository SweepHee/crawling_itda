package security.security.Mapper;

import org.apache.ibatis.annotations.Mapper;
import security.security.Vo.ContentsVo;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface ContentsMapper {

    public void create(List<ContentsVo> contentsVos);
    public boolean isUrl(HashMap<String, String> params);
    public void createMaster(ContentsVo contentsVo);

}
