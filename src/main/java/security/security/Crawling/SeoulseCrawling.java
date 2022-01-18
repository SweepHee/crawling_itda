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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SeoulseCrawling implements Crawling {

    @Autowired
    ContentsMapper contentsMapper;

    @Autowired
    Environment environment;

    /*
     * 서울사회적기업협의회
     * http://www.seoulse.kr/
     *  */

    private String url = "http://www.seoulse.kr/bbs/board.php?bo_table=notice&page=";
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

        WebDriver driver = new ChromeDriver(service);
        WebDriver driver2 = new ChromeDriver(service);
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebDriverWait wait2 = new WebDriverWait(driver, 10);

        List<ContentsVo> contentsVos = new ArrayList<>();


        for (int i=page; i>0; i--) {

            driver.get(url + i);

            for(int j=1; j<16; j++) {

                try {

                    //*[@id="list-body"]/li[9]/div[2]/a/span

                    /* li not selector */
//                    WebElement titleXpath = driver.findElement(By.xpath("//*[@id=\"list-body\"]/li[not(contains(@class, 'bg-light'))]["+j+"]/div[2]/a/span"));
                    WebElement bodyUrlXpath = driver.findElement(By.xpath("//*[@id=\"list-body\"]/li[not(contains(@class, 'bg-light'))]["+j+"]/div[2]/a"));

                    String title = bodyUrlXpath.getText();
                    String url = bodyUrlXpath.getAttribute("href");

                    Pattern p = Pattern.compile("\\[(.*?)\\]");
                    Matcher m = p.matcher(title);
                    ArrayList<String> pattern = new ArrayList<String>();

                    while (m.find()) {
                        pattern.add(m.group());
                    }

                    driver2.get(url + i);
                    WebElement targettypeXpath = driver2.findElement(By.xpath("//*[@id=\"page\"]/div[1]/section/div/div/section/article/div[1]/div[1]/div/span[2]"));

                    String targettype = targettypeXpath.getText();

                    /* 글 상세페이지 들어가므로 1500 기다림 */
                    Thread.sleep(1500);
//                    driver2.quit();

                    ContentsVo vo = new ContentsVo();
                    vo.setTargetname("서울사회적기업협의회");
                    vo.setTargetnamecode("임의코드");
                    vo.setTargettype(targettype);
                    vo.setTargettypecode(targettype);
                    vo.setTargetcost("-");
                    vo.setLoccode("C02");
                    vo.setTitle(title);
                    vo.setBodyurl(url);
                    vo.setEndTime("");

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
