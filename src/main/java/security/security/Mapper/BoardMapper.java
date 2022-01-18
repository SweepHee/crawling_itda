package security.security.Mapper;

import org.apache.ibatis.annotations.Mapper;
import security.security.Vo.BoardVo;
import security.security.Vo.MemberVo;

import java.util.List;

@Mapper
public interface BoardMapper {

    public List<BoardVo> getList();

}
