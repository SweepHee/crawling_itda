package security.security.Crawling;

import org.openqa.selenium.By;
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
public class SeoulKoccaCrawling implements Crawling {

    @Autowired
    ContentsMapper contentsMapper;

    @Autowired
    Environment environment;

    /*
     * 한국콘텐츠진흥원
     * https://www.kocca.kr/
     *  */

    private String url = "https://m.home.kocca.kr/mcop/pims/list.do?siteId=&category=&menuNo=201290&searchWrd=&search=&sortOrdrBy=&recptSt=&pageIndex=";
    private int page = 2;

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

        WebDriver driver = new ChromeDriver(service);
        WebDriverWait wait = new WebDriverWait(driver, 10);

        List<ContentsVo> contentsVos = new ArrayList<>();


        for (int i=page; i>0; i--) {

            driver.get(url + i);

            for(int j=1; j<13; j++) {

                try {

                    WebElement titleXpath = driver.findElement(By.xpath("//*[@id=\"frm\"]/div[2]/div/dl[" + j + "]/dt/a"));

                    String title = titleXpath.getText();
                    String bodyUrl = titleXpath.getAttribute("href");

                    ContentsVo vo = new ContentsVo();
                    vo.setTargetname("한국콘텐츠진흥원");
                    vo.setTargetnamecode("임의코드");
                    vo.setTargettype("-");
                    vo.setTargettypecode("-");
                    vo.setTargetcost("-");
                    vo.setLoccode("C02");
                    vo.setTitle(title);
                    vo.setBodyurl(bodyUrl);
                    vo.setEndTime("");
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

            Thread.sleep(500);
        }

        /* 빈 리스트가 아니면 크레이트 */
        if (!contentsVos.isEmpty()) {
            contentsMapper.create(contentsVos);
        }

        driver.quit();
        service.stop();
    }


}
