package security.security.Config.Controller;


import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import security.security.Crawling.*;

@Controller
public class CrawlingController {

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


    /* 서울시 청년활동지원센터 */
    @Autowired
    SeoulBiUosCrawling seoulBiUosCrawling;

    /* 성북구 중장년 기술창업센터 */
    @Autowired
    SeoulSsscCrawling seoulSsscCrawling;

    /* 송파구일자리통합지원센터 */
    @Autowired
    SeoulSongpaCrawling seoulSongpaCrawling;

    /* 한국콘텐츠진흥원 */
    @Autowired
    SeoulKoccaCrawling seoulKoccaCrawling;

    /* 가톨릭대학교 창업대학 */
    @Autowired
    SeoulCatholicCrawling seoulCatholicCrawling;

    /* 양천디지털상상캠퍼스 */
    @Autowired
    SeoulYcstartupCrawling seoulYcstartupCrawling;

    /* 건국대학교 창업지원단 */
    @Autowired
    SeoulKonkukCrawling seoulKonkukCrawling;

    /* 건국대학교 창업보육센터 */
    @Autowired
    SeoulKkuCrawling seoulKkuCrawling;

    /* 서울창업카페신촌점 */
    @Autowired
    SeoulStartupcafeCrawling seoulStartupcafeCrawling;




    /* 서울소셜벤처허브 */
    @Autowired
    SeoulSvhcCrawling seoulSvhcCrawling;

    /* 한국사회적기업진흥원 */
    @Autowired
    SeoulSocialEnterPrise seoulSocialEnterPrise;

    /* 서울창업카페상봉점 */
    @Autowired
    SeoulSangbongCrawling seoulSangbongCrawling;

    /* 서울창업디딤터 */
    @Autowired
    SeoulDidimentoCrawling seoulDidimentoCrawling;

    /* 한양대학교창업지원단 */
    @Autowired
    SeoulHanyangCrawling seoulHanyangCrawling;

    /* 카이스트창업원 */
    @Autowired
    SeoulKaistCrawling seoulKaistCrawling;

    /* 연세대학교 창업지원관 */
    @Autowired
    SeoulYonseiCrawling seoulYonseiCrawling;


    @GetMapping("/craw")
    public String index() throws InterruptedException {
//        sbaCrawling.craw();
//        seoulSbdcCrawling.craw();
//        sygcCrawling.craw();
//        seoulBiUosCrawling.craw();
//        seoulSsscCrawling.craw();

//        seoulKkuCrawling.craw();
//        seoulSvhcCrawling.craw();
//        seoulSocialEnterPrise.craw();


        seoulYonseiCrawling.craw();

        return "/craw";
    }

}