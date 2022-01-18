package security.security.Service.Impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import security.security.Mapper.BoardMapper;
import security.security.Service.BoardService;
import security.security.Vo.BoardVo;

import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    BoardMapper boardMapper;


    @Override
    public List<BoardVo> getList() {
        return boardMapper.getList();
    }
}
