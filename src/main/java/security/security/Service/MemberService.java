package security.security.Service;


import org.springframework.stereotype.Service;
import security.security.Vo.MemberVo;

import java.util.List;


public interface MemberService {

    public MemberVo findById(String id);
    public List<MemberVo> getList();
    public List<MemberVo> getPaginationList(int offset, int limit);
    public int getTotalCount();

    public List<MemberVo> getListRegexp(String regex);

    public List<MemberVo> findKeyAndValue(String key, String searchValue);

}
