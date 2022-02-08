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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SeoulYonseiCrawling implements Crawling {

    @Autowired
    ContentsMapper contentsMapper;

    @Autowired
    Environment environment;

    /*
     * 연세대학교 창업지원관
     * https://venture.yonsei.ac.kr/
     *  */

    private String url = "https://venture.yonsei.ac.kr/notice/?q=YToxOntzOjEyOiJrZXl3b3JkX3R5cGUiO3M6MzoiYWxsIjt9&page=";
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

        ContentsVo contentsVo = new ContentsVo();
        contentsVo.setTitle("연세대학교창업지원관");
        contentsVo.setUrl("https://venture.yonsei.ac.kr/");
        contentsVo.setLocation("C02");
        contentsVo.setActiveYn("Y");
        contentsVo.setErrorYn("N");
        List<ContentsVo> contentsVos = new ArrayList<>();


        for (int i=page; i>0; i--) {

            driver.get(url + i);
            Thread.sleep(1000);
            for(int j=2; j<22; j++) {
                    try {

                        WebElement titleXpath = driver.findElement(By.xpath("//*[@id='w20191011d845845dcd57c']/div/div[2]/div[2]/ul[" + j + "]/li[4]/a[2]/span"));
                        WebElement typeXpath = driver.findElement(By.xpath("//*[@id='w20191011d845845dcd57c']/div/div[2]/div[2]/ul[" + j + "]/li[3]/a"));
                        WebElement urlXpath = driver.findElement(By.xpath(" //*[@id='w20191011d845845dcd57c']/div/div[2]/div[2]/ul[11]/li[4]/a[2]"));

                        String title = titleXpath.getText();
                        String bodyurl = urlXpath.getAttribute("href");
                        String type = typeXpath.getText();


                        ContentsVo vo = new ContentsVo();

                        vo.setTargetname("연세대학교창업지원관");
                        vo.setTargetnamecode("임의코드");
                        vo.setTargettype(type);
                        vo.setTargettypecode(type);
                        vo.setTargetcost("-");
                        vo.setLoccode("C02");
                        vo.setTitle(title);
                        vo.setBodyurl(bodyurl);
                        vo.setEndTime("");
                        System.out.println(vo.toString());
                        HashMap<String, String> params = new HashMap<>();
                        params.put("bodyurl", bodyurl);
                        boolean isUrl = contentsMapper.isUrl(params);
                        if (!isUrl) {
                            contentsVos.add(vo);
                        }

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        contentsVo.setErrorYn("Y");
                        contentsMapper.createMaster(contentsVo);
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
