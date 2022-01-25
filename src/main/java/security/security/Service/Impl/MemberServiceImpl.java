//package security.security.Service.Impl;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import security.security.Mapper.MemberMapper;
//import security.security.Service.MemberService;
//import security.security.Vo.MemberVo;
//
//import java.util.List;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class MemberServiceImpl implements MemberService, UserDetailsService {
//
//    @Autowired
//    MemberMapper memberMapper;
//
//    @Override
//    public MemberVo findById(String id) {
//        return memberMapper.findById(id);
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
//
//        MemberVo memberVo = memberMapper.findById(id);
//        System.out.println(memberVo.toString());
//        if (memberVo == null) {
//            throw new UsernameNotFoundException(id);
//        }
//
//        return User.builder()
//                .username(memberVo.getId())
//                .password(memberVo.getPassword())
//                .roles("ADMIN")
//                .build();
//    }
//
//
//    @Override
//    public List<MemberVo> getList() {
//        return memberMapper.getList();
//    }
//
//    @Override
//    public List<MemberVo> getPaginationList(int offset, int limit) {
//        return memberMapper.getPaginationList(offset, limit);
//    }
//
//    @Override
//    public int getTotalCount() {
//        return memberMapper.getTotalCount();
//    }
//
//    @Override
//    public List<MemberVo> getListRegexp(String regex) {
//        return memberMapper.getListRegexp(regex);
//    }
//
//    @Override
//    public List<MemberVo> findKeyAndValue(String key, String searchValue) {
//        return memberMapper.findKeyAndValue(key, searchValue);
//    }
//
//}
