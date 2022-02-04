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
public class SeoulStartupcafeCrawling implements Crawling {

    @Autowired
    ContentsMapper contentsMapper;

    @Autowired
    Environment environment;

    /*
     * 서울창업카페신촌점
     * http://seoulstartupcafe.com/
     *  */

    private String url = "http://seoulstartupcafe.com/?q=YToxOntzOjEyOiJrZXl3b3JkX3R5cGUiO3M6MzoiYWxsIjt9&page=";
    private int page = 3;

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
        contentsVo.setTitle("서울창업카페신촌점");
        contentsVo.setUrl("http://seoulstartupcafe.com");
        contentsVo.setLocation("C02");
        contentsVo.setActiveYn("Y");
        contentsVo.setErrorYn("N");
        List<ContentsVo> contentsVos = new ArrayList<>();


        for (int i=page; i>0; i--) {

            driver.get(url + i);

            for(int j=1; j<11; j++) {

                try {

                    WebElement titleXpath = driver.findElement(By.xpath("//*[@id=\"post_card_b202011251f4f5cd95147f\"]/div[" + j + "]/div/span/div[2]/div/div[1]"));
                    WebElement targetXpath = driver.findElement(By.xpath("//*[@id=\"post_card_b202011251f4f5cd95147f\"]/div[" + j + "]/div/span/div[2]/div/div[1]/a/em"));
                    WebElement urlXpath = driver.findElement(By.xpath("//*[@id=\"post_card_b202011251f4f5cd95147f\"]/div[" + j + "]/div/span/div[2]"));

                    String title = titleXpath.getText();
                    String target = targetXpath.getText();
                    String url = urlXpath.getAttribute("onclick");

                    String targettype = target.replaceAll("#", "");
                    String bodyurl = "http://seoulstartupcafe.com/" + url.replaceAll("location.href=","").replaceAll("'","");

                    ContentsVo vo = new ContentsVo();
                    vo.setTargetname("서울창업카페신촌점");
                    vo.setTargetnamecode("임의코드");
                    vo.setTargettype(targettype);
                    vo.setTargettypecode(targettype);
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
