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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SbaCrawling implements Crawling {

    @Autowired
    ContentsMapper contentsMapper;

    @Autowired
    Environment environment;

    /*
     * 서울산업진흥원
     * http://new.sba.kr/
     * 게시글 페이지가 POST로 되어 있어서 자바스크립트 함수 실행 처리를 했음
     *  */

    private String url = "https://new.sba.kr/web/contents/contentsListPage1.do?category1=COMPANY_SUPPORT&category2=01";
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
        contentsVo.setTitle("서울산업진흥원");
        contentsVo.setUrl("http://new.sba.kr/");
        contentsVo.setLocation("C02");
        contentsVo.setActiveYn("Y");
        contentsVo.setErrorYn("N");

        List<ContentsVo> contentsVos = new ArrayList<>();

        driver.get(url);
        Thread.sleep(1500);

        for (int i=page; i>0; i--) {

            jse.executeScript("pageMove("+ i +");");
            Thread.sleep(1500);

            for(int j=1; j<13; j++) {

                try {

                    WebElement titleXpath = driver.findElement(By.xpath("//*[@id=\"webContentDataList\"]/li["+j+"]/a/dl/dd[1]/h3"));
                    String baseUrl = "https://new.sba.kr/web/contents/contentsDetailPage1.do?cont_idx=";

                    WebElement bodyUrlXpath = driver.findElement(By.xpath("//*[@id=\"webContentDataList\"]/li["+j+"]/a"));
                    String urlHref = bodyUrlXpath.getAttribute("href");

                    String cont_idx = urlHref.replaceAll("[^0-9]","");

                    WebElement targetTypeXpath = driver.findElement(By.xpath("//*[@id=\"webContentDataList\"]/li["+j+"]/ul/li[1]/span"));
                    String targettype = targetTypeXpath.getText();

                    WebElement endTimeXpath = driver.findElement(By.xpath("//*[@id=\"webContentDataList\"]/li["+ j +"]/dl/dd[2]"));

                    String title = titleXpath.getText();
                    String url = baseUrl + cont_idx;
                    String[] endTimeSplit = endTimeXpath.getText().split("~");
                    String endTime = endTimeSplit[1];

                    ContentsVo vo = new ContentsVo();
                    vo.setTargetname("서울산업진흥원");
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
                    contentsVo.setErrorYn("Y");
                    contentsMapper.createMaster(contentsVo);
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
