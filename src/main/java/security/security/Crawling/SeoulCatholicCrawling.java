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
public class SeoulCatholicCrawling implements Crawling {

    @Autowired
    ContentsMapper contentsMapper;

    @Autowired
    Environment environment;

    /*
     * 가톨릭대학교 창업대학
     * https://startup.catholic.ac.kr/
     *  */

    private String url = "https://startup.catholic.ac.kr/front/boardlist.do?bbsConfigFK=586&cmsDirPkid=5154&cmsLocalPkid=0&searchField=ALL&searchValue=&currentPage=";
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

        ContentsVo contentsVo = new ContentsVo();
        contentsVo.setTitle("가톨릭대학교창업대학");
        contentsVo.setUrl("https://startup.catholic.ac.kr/");
        contentsVo.setLocation("C02");
        contentsVo.setActiveYn("Y");
        contentsVo.setErrorYn("N");

        List<ContentsVo> contentsVos = new ArrayList<>();

        driver.get(url);
        Thread.sleep(1500);

        for (int i=page; i>0; i--) {

            driver.get(url + i);

            for(int j=1; j<10; j++) {

                try {


                    WebElement titleXpath = driver.findElement(By.xpath(" //*[@id=\"contents\"]/div/div[2]/ul/li[" + j + "]/a/div[1]/div/div"));
                    WebElement bodyUrlXpath = driver.findElement(By.xpath("//*[@id=\"contents\"]/div/div[2]/ul/li[" + j + "]/a"));

                    String title = titleXpath.getText();
                    String url = bodyUrlXpath.getAttribute("href");

                    ContentsVo vo = new ContentsVo();
                    vo.setTargetname("가톨릭대학교 창업대학");
                    vo.setTargetnamecode("임의코드");
                    vo.setTargettype("-");
                    vo.setTargettypecode("-");
                    vo.setTargetcost("-");
                    vo.setLoccode("C02");
                    vo.setTitle(title);
                    vo.setBodyurl(url);
                    vo.setEndTime("-");

                    HashMap<String, String> params = new HashMap<>();
                    params.put("bodyurl", url);
                    boolean isUrl = contentsMapper.isUrl(params);
                    if (!isUrl) {
                        contentsVos.add(vo);
                    }

                } catch (Exception e) {
                    contentsVo.setErrorYn("Y");
                    contentsMapper.createMaster(contentsVo);
                    System.out.println(e.getMessage());
                }

            }

            Thread.sleep(500);
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
