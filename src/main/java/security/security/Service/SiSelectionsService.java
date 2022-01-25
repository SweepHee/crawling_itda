package security.security.Service;

import security.security.Vo.SiSelectionsVO;

import java.util.List;

public interface SiSelectionsService {

    public void upload(List<SiSelectionsVO> vos);
    public boolean isName(SiSelectionsVO vo);
    public void update(SiSelectionsVO vo);

}
