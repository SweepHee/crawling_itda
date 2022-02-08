package security.security.Crawling;

import org.springframework.core.env.Environment;
import org.checkerframework.checker.units.qual.A;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import security.security.Mapper.ContentsMapper;
import security.security.Vo.ContentsVo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SbscCrawling implements Crawling {

    @Autowired
    ContentsMapper contentsMapper;

    @Autowired
    Environment environment;

    /*
    *
    *  */

    private String url = "https://sbsc.seoul.go.kr/fe/support/seoul/NR_list.do?bbsCd=1&bbsSeq=&searchVals=&bbsGrpCds_all=on&orgCd=&currentPage=";
    private int page = 1;

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public void craw() {

        String driverPath = environment.getProperty("chrome.driver.path");
        File driverFile = new File(String.valueOf(driverPath));

        if (!driverFile.exists() && driverFile.isFile()) {
            throw new RuntimeException("Not found");
        }

        ChromeDriverService service = new ChromeDriverService.Builder()
                .usingDriverExecutable(driverFile)
                .usingAnyFreePort()
                .build();

        try {
            service.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        WebDriver driver = new ChromeDriver(service);
        WebDriverWait wait = new WebDriverWait(driver, 10);
        System.out.println("K-STARTUP 시작");

        ContentsVo contentsVo = new ContentsVo();
        contentsVo.setTitle("서울기업지원센터");
        contentsVo.setUrl("https://sbsc.seoul.go.kr/");
        contentsVo.setLocation("C02");
        contentsVo.setActiveYn("Y");
        contentsVo.setErrorYn("N");

        List<ContentsVo> contentsVos = new ArrayList<>();

        try {
            for (int i=page; i>0; i--) {
                System.out.println("페이지::" + i);
                driver.get(url + i);

                for(int j=1; j<11; j++) {
                    WebElement titleXpath = driver.findElement(By.xpath("//*[@id=\"container\"]/div/div[2]/table/tbody/tr["+j+"]/td[2]/a"));
                    WebElement targetTypeXpath = driver.findElement(By.xpath("//*[@id=\"container\"]/div/div[2]/table/tbody/tr["+j+"]/td[3]"));
                    WebElement endTimeXpath = driver.findElement(By.xpath("//*[@id=\"container\"]/div/div[2]/table/tbody/tr["+j+"]/td[4]"));

                    String title = titleXpath.getText();
                    String targettype = targetTypeXpath.getText();
                    String endtime = endTimeXpath.getText();

                    String url = titleXpath.getAttribute("onclick");
                    String intStr = url.replaceAll("[^0-9]", "");

                    String bodyurl = "https://sbsc.seoul.go.kr/fe/support/seoul/NR_view.do?bbsCd=1&bbsSeq=" + intStr;

                    ContentsVo vo =new ContentsVo();
                    vo.setTargetname("서울기업지원센터");
                    vo.setTargetnamecode("임의코드");
                    vo.setTargettype(targettype);
                    vo.setTargettypecode(targettype);
                    vo.setTargetcost("-");
                    vo.setLoccode("C02");
                    vo.setTitle(title);
                    vo.setBodyurl(bodyurl);
                    vo.setEndTime(endtime);

                    HashMap<String, String> params = new HashMap<>();
                    params.put("bodyurl", bodyurl);
                    boolean isUrl = contentsMapper.isUrl(params);
                    System.out.println("이즈유알엘::" + isUrl);
                    if (!isUrl) {
                        contentsVos.add(vo);
                    }

                }

                Thread.sleep(500);
            }

        } catch (Exception e) {
            contentsVo.setErrorYn("Y");
            contentsMapper.createMaster(contentsVo);
            e.printStackTrace();
        } finally {

            /* 빈 리스트가 아니면 크레이트 */
            if (!contentsVos.isEmpty()) {
                try{
                    contentsMapper.create(contentsVos);
                    contentsMapper.createMaster(contentsVo);
                }catch (Exception e){
                    contentsVo.setErrorYn("Y");
                    contentsMapper.createMaster(contentsVo);
                }
            }

            driver.quit();
            service.stop();
        }

    }


}
