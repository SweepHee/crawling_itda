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
public class SeoulSehubCrawling implements Crawling {

    @Autowired
    ContentsMapper contentsMapper;

    @Autowired
    Environment environment;

    /*
     * 서울시청년활동지원센터
     * https://www.sygc.kr/
     *  */

    private String url = "https://sehub.net/archives/category/alarm/opencat/page/";
    private int page = 5;

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

        JavascriptExecutor jse = (JavascriptExecutor) driver;

        List<ContentsVo> contentsVos = new ArrayList<>();

        driver.get(url);
        Thread.sleep(1500);

        for (int i=page; i>0; i--) {

            driver.get(url + i);

            for(int j=1; j<10; j++) {

                try {

                    WebElement titleXpath = driver.findElement(By.xpath("/html/body/main/div[1]/ul/li["+j+"]/a[1]/div[3]/h3"));
                    WebElement bodyUrlXpath = driver.findElement(By.xpath("/html/body/main/div[1]/ul/li["+j+"]/a[1]"));
                    WebElement endTimeXpath = driver.findElement(By.xpath("/html/body/main/div[1]/ul/li["+j+"]/a[1]/div[3]/ul/li[1]/span[2]"));
                    WebElement targetTypeXpath = driver.findElement(By.xpath("/html/body/main/div[1]/ul/li["+j+"]/a[1]/div[2]/div[1]"));

                    String title = titleXpath.getText();
                    String url = bodyUrlXpath.getAttribute("href");
                    String targettype = targetTypeXpath.getText();
                    String endTime = endTimeXpath.getText();


                    ContentsVo vo = new ContentsVo();
                    vo.setTargetname("서울시청년활동지원센터");
                    vo.setTargetnamecode("임의코드");
                    vo.setTargettype(targettype);
                    vo.setTargettypecode(targettype);
                    vo.setTargetcost("-");
                    vo.setLoccode("C02");
                    vo.setTitle(title);
                    vo.setBodyurl(url);
                    vo.setEndTime(endTime);

                    HashMap<String, String> params = new HashMap<>();
                    params.put("bodyurl", url);
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
