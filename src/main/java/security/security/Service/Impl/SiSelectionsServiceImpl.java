package security.security.Service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import security.security.Mapper.SiSelectionsMapper;
import security.security.Service.SiSelectionsService;
import security.security.Vo.SiSelectionsVO;

import java.util.List;

@Service
public class SiSelectionsServiceImpl implements SiSelectionsService {

    @Autowired
    SiSelectionsMapper siSelectionsMapper;

    @Override
    public void upload(List<SiSelectionsVO> vos) {
        siSelectionsMapper.upload(vos);
    }

    @Override
    public boolean isName(SiSelectionsVO vo) {
        return siSelectionsMapper.isName(vo);
    }

    @Override
    public void update(SiSelectionsVO vo) {
        siSelectionsMapper.update(vo);
    }
}
