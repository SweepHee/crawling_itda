package security.security.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import security.security.Crawling.*;

@Controller
public class CrawlingController {

    @Autowired
    Upbeat upbeat;

    @Autowired
    TestCrawling testCrawling;

    @Autowired
    RcdaCrawling rcdaCrawling;

    @Autowired
    KStartUpCrawling kStartUpCrawling;

    @Autowired
    SbscCrawling sbscCrawling;

    @Autowired
    YouthSeoulCrawling youthSeoulCrawling;

    @Autowired
    SeoulseCrawling seoulseCrawling;

    /* 서울산업진흥원 */
    @Autowired
    SbaCrawling sbaCrawling;

    /* 서울시 자영업지원센터 */
    @Autowired
    SeoulSbdcCrawling seoulSbdcCrawling;

    /* 서울시 청년활동지원센터 */
    @Autowired
    SygcCrawling sygcCrawling;


    @GetMapping("/craw")
    public String index() throws InterruptedException {
//        sbaCrawling.craw();
//        seoulSbdcCrawling.craw();
        sygcCrawling.craw();
        return "/craw";
    }

}