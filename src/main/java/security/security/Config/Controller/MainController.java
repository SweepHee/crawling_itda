package security.security.Config.Controller;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import security.security.Global.GlobalFunction;
import security.security.Service.BoardService;
import security.security.Service.ExcelService;
import security.security.Service.MemberService;
import security.security.Service.SiSelectionsService;
import security.security.Vo.BoardVo;
import security.security.Vo.ImageLink;
import security.security.Vo.MemberVo;
import security.security.Vo.SiSelectionsVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
public class MainController {

//    @Autowired
//    MemberService memberService;

    @Autowired
    BoardService boardService;

    @Autowired
    ExcelService excelService;

    @Autowired
    SiSelectionsService siSelectionsService;

//    @Autowired
//    PasswordEncoder passwordEncoder;

    @Autowired
    GlobalFunction globalFunction;

    @GetMapping("/")
    public String test() {
        return "test";
    }
    

//    @GetMapping("/")
//    public String index(Model model, HttpServletRequest request) {
//
//        int page = request.getParameter("page") != null
//                ? Integer.parseInt(request.getParameter("page"))
//                : 1;
//        int limit = 5;
//        int offset = (page-1) * limit;
//
//        List<MemberVo> memberVoList = memberService.getPaginationList(offset, limit);
//        List<MemberVo> memberVoRegexList = memberService.getListRegexp("bbit|og|ca");
//
//        List<MemberVo> memberVoKeyAndValueList = memberService.findKeyAndValue("id", "cat");
//
//
//        int totalCount = memberService.getTotalCount();
//
//        int lastPage = (int) Math.ceil(totalCount/limit);
//
//        String pageExcludeQueryString = globalFunction.splitQueryString(request, "page");
//
//
//
//
//        model.addAttribute("lists", memberVoList);
//        model.addAttribute("totalCount", totalCount);
//        model.addAttribute("page", page);
//        model.addAttribute("lastPage", lastPage);
//        model.addAttribute("queryString", pageExcludeQueryString);
//
//        return "index";
//    }

//    @GetMapping("/async")
//    public String async(HttpServletRequest request, Model model) {
//
//        int page = request.getParameter("page") != null
//                ? Integer.parseInt(request.getParameter("page"))
//                : 1;
//
//        int limit = 5;
//        int offset = (page-1) * limit;
//        List<MemberVo> memberVoList = memberService.getPaginationList(offset, limit);
//
//        String pageExcludeQueryString = globalFunction.splitQueryString(request, "page");
//
//        int totalCount = memberService.getTotalCount();
//
//        int lastPage = (int) Math.ceil(totalCount/limit);
//
//        model.addAttribute("lists", memberVoList);
//        model.addAttribute("totalCount", totalCount);
//        model.addAttribute("page", page);
//        model.addAttribute("lastPage", lastPage);
//        model.addAttribute("paginationAsync", false);
//        model.addAttribute("queryString", pageExcludeQueryString);
//
//        return "async";
//    }


//    @GetMapping("/async/list")
//    public String asyncGetList(HttpServletRequest request, Model model) {
//
//        int page = Integer.parseInt(request.getParameter("page"));
//        int limit = 5;
//        int offset = (page-1) * limit;
//        List<MemberVo> memberVoList = memberService.getPaginationList(offset, limit);
//
//        model.addAttribute("lists", memberVoList);
//
//        return "component/list";
//    }
//
//
//    @GetMapping("/async/pagination")
//    public String pagination(HttpServletRequest request, Model model) {
//
//        int limit = 5;
//        int totalCount = memberService.getTotalCount();
//        int lastPage = (int) Math.ceil(totalCount/limit);
//
//        model.addAttribute("page", request.getParameter("page"));
//        model.addAttribute("keyword", request.getParameter("keyword"));
//        model.addAttribute("totalCount", totalCount);
//        model.addAttribute("lastPage", lastPage);
//        model.addAttribute("range", 3);
//
//        return "component/pagination";
//
//    }


    @GetMapping("/login")
    public String login() {

//        MemberVo memberVo = memberService.findById("test");
//        System.out.println(memberVo.toString());

//        List<BoardVo> boardVo = boardService.getList();
//        System.out.println(boardVo.toString());

//        System.out.println(passwordEncoder.encode("1234"));


        return "login";
    }


    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/fail")
    public String fail() {
        return "fail";
    }

    @GetMapping("/create")
    public String create() {
        return "create";
    }



    @PostMapping("/editor/upload")
    @ResponseBody
    public ImageLink uploadImage(@RequestParam("file") MultipartFile multi, HttpServletRequest request) throws Exception {

        System.out.println("hello world");

//        try {
//
//            //String uploadpath = request.getServletContext().getRealPath(path);
//            String uploadpath = "/upload/img/";
//            String originFilename = multi.getOriginalFilename();
//            String extName = originFilename.substring(originFilename.lastIndexOf("."),originFilename.length());
//            long size = multi.getSize();
//
//            String saveFileName = "";
//
//            Calendar calendar = Calendar.getInstance();
//            saveFileName += calendar.get(Calendar.YEAR);
//            saveFileName += calendar.get(Calendar.MONTH);
//            saveFileName += calendar.get(Calendar.DATE);
//            saveFileName += calendar.get(Calendar.HOUR);
//            saveFileName += calendar.get(Calendar.MINUTE);
//            saveFileName += calendar.get(Calendar.SECOND);
//            saveFileName += calendar.get(Calendar.MILLISECOND);
//            saveFileName += extName;
//
//
//            System.out.println("uploadpath : " + uploadpath);
//            System.out.println("originFilename : " + originFilename);
//            System.out.println("extensionName : " + extName);
//            System.out.println("size : " + size);
//            System.out.println("saveFileName : " + saveFileName);
//
//
//            ArrayList<String> imgUrl = new ArrayList<>();
//
//            String url = "";
//            ImageLink link = new ImageLink();
//            if(!multi.isEmpty())
//            {
//                File file = new File(uploadpath, saveFileName);
//                if(!file.exists()) // 해당 경로가 없을 경우
//                    file.mkdirs();  // 폴더 생성
//                multi.transferTo(file);
//                url = "/upload/img/" + saveFileName;
//                link.setFileName(saveFileName);
//                link.setUrl(url);
//            }
//
//            return link;
//
//        }catch(Exception e)
//        {
//            System.out.println(e.getMessage());
//        }
        return null;
    }

    @GetMapping("/excel")
    public String excel() {
        return "excel";
    }


    @PostMapping("/excel/read")
    public String readExcel (@RequestParam("file") MultipartFile file) throws IOException {
        excelService.uploadSiSelectionsList(file);
        return "redirect:/excel";
    }



}
