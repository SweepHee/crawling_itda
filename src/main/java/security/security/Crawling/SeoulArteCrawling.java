package security.security.Crawling;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import security.security.Mapper.ContentsMapper;
import security.security.Vo.ContentsVo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class SeoulArteCrawling implements Crawling {

    @Autowired
    ContentsMapper contentsMapper;

    @Autowired
    Environment environment;

    /*
     * 한국문화예술교육진흥원
     * https://www.arte.or.kr/
     *  */

    private String url = "https://www.arte.or.kr";
    private int page = 1;

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public void craw() throws InterruptedException {

        String driverPath = environment.getProperty("chrome.driver.path");
        File driverFile = new File(String.valueOf(driverPath));

        String driverFilePath = driverFile.getAbsolutePath();
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

        /* 작업중 완료안됨 */

        WebDriver driver = new ChromeDriver(service);
        WebDriverWait wait = new WebDriverWait(driver, 10);

        JavascriptExecutor jse = (JavascriptExecutor) driver;

        List<ContentsVo> contentsVos = new ArrayList<>();

        ContentsVo contentsVo = new ContentsVo();
        contentsVo.setTitle("한국문화예술교육진흥원");
        contentsVo.setUrl("https://www.arte.or.kr");
        contentsVo.setLocation("C02");
        contentsVo.setActiveYn("Y");
        contentsVo.setErrorYn("N");

        driver.get(url);
        Thread.sleep(1500);

        for (int i=page; i>0; i--) {

            Thread.sleep(1500);

            for(int j=1; j<16; j++) {

                try {

                    WebElement titleXpath = driver.findElement(By.xpath("//*[@id=\"frm\"]/div/div[3]/table/tbody/tr["+j+"]/td[2]/a"));

                    String baseUrl = "https://www.arte.or.kr/notice/business/notice/Business_BoardView.do?board_id=";

                    /* 숫자만 남기기 */
                    String url = titleXpath.getAttribute("onclick");
                    String intStr = url.replaceAll("goViewPage2", "").replaceAll("[^0-9]", "");

                    String bodyUrl = baseUrl + intStr;

                    String title = titleXpath.getText();
                    String targettype = "사업공고";

                    ContentsVo vo = new ContentsVo();
                    vo.setTargetname("한국사회적기업진흥원");
                    vo.setTargetnamecode("임의코드");
                    vo.setTargettype(targettype);
                    vo.setTargettypecode(targettype);
                    vo.setTargetcost("-");
                    vo.setLoccode("C02");
                    vo.setTitle(title);
                    vo.setBodyurl(bodyUrl);
                    vo.setEndTime("");

                    System.out.println(vo.toString());

                    HashMap<String, String> params = new HashMap<>();
                    params.put("bodyurl", bodyUrl);
                    boolean isUrl = contentsMapper.isUrl(params);
                    if (!isUrl) {
                        contentsVos.add(vo);
                    }


                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }

        }


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
