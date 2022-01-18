package security.security.Mapper;

import org.apache.ibatis.annotations.Mapper;
import security.security.Vo.MemberVo;

import java.util.List;

@Mapper
public interface MemberMapper {

    public MemberVo findById(String id);

    public List<MemberVo> getList();

    public int getTotalCount();

    public List<MemberVo> getPaginationList(int offset, int limit);

    public List<MemberVo> getListRegexp(String regex);

    public List<MemberVo> findKeyAndValue(String key, String searchValue);

}
