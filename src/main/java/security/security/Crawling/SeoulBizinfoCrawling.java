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
public class SeoulBizinfoCrawling implements Crawling {

    @Autowired
    ContentsMapper contentsMapper;

    @Autowired
    Environment environment;

    /*
     * 기업마당
     * https://m.bizinfo.go.kr/
     *  */

    private String url = "https://m.bizinfo.go.kr/sem/sema/selectSEMA100View.do?pageIndex=";
    private int page = 100;

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
        contentsVo.setTitle("기업마당");
        contentsVo.setUrl("https://m.bizinfo.go.kr/");
        contentsVo.setLocation("C02");
        contentsVo.setActiveYn("Y");
        contentsVo.setErrorYn("N");
        List<ContentsVo> contentsVos = new ArrayList<>();


        for (int i=page; i>0; i--) {

            driver.get(url + i);
            Thread.sleep(1000);
            for(int j=2; j<11; j++) {
                    try {
                        WebElement titleXpath = driver.findElement(By.xpath("/html/body/div[1]/div[5]/form[2]/section/div[2]/div[4]/ul/li[" + j + "]/a/span[1]"));
                        WebElement targetXpath = driver.findElement(By.xpath("/html/body/div[1]/div[5]/form[2]/section/div[2]/div[4]/ul/li[" + j + "]/a/span[2]/span[1]"));
                        WebElement dateXpath = driver.findElement(By.xpath("/html/body/div[1]/div[5]/form[2]/section/div[2]/div[4]/ul/li[" + j + "]/a/span[3]"));
                        WebElement urlXpath = driver.findElement(By.xpath("/html/body/div[1]/div[5]/form[2]/section/div[2]/div[4]/ul/li[" + j + "]/a"));

                        ContentsVo vo = new ContentsVo();

                        String title = titleXpath.getText();
                        String bodyurl = "https://m.bizinfo.go.kr/sem/sema/selectSEMA140Detail.do?pblancId=" + urlXpath.getAttribute("id").replaceAll("pblancId_","");
                        String endtime = dateXpath.getText().replaceAll("기간:","");
                        String target = targetXpath.getText();


                        vo.setTargetname("기업마당");
                        vo.setTargetnamecode("임의코드");
                        vo.setTargetcost("-");
                        vo.setLoccode("C02");
                        vo.setTargettype(target);
                        vo.setTargettypecode(target);
                        vo.setTitle(title);
                        vo.setBodyurl(bodyurl);
                        vo.setEndTime(endtime);
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
