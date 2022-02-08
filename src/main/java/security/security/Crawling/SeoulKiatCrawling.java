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
public class SeoulKiatCrawling implements Crawling {

    @Autowired
    ContentsMapper contentsMapper;

    @Autowired
    Environment environment;

    /*
     * 한국산업기술진흥원
     * https://www.kiat.or.kr/
     * 게시글 페이지가 POST로 되어 있어서 자바스크립트 함수 실행 처리를 했음
     *  */

    private String url = "https://www.kiat.or.kr/front/board/boardContentsListPage.do?board_id=90&MenuId=b159c9dac684471b87256f1e25404f5e";
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

        JavascriptExecutor jse = (JavascriptExecutor) driver;

        ContentsVo contentsVo = new ContentsVo();
        contentsVo.setTitle("한국산업기술진흥원");
        contentsVo.setUrl("https://www.kiat.or.kr/");
        contentsVo.setLocation("C02");
        contentsVo.setActiveYn("Y");
        contentsVo.setErrorYn("N");

        List<ContentsVo> contentsVos = new ArrayList<>();

        driver.get(url);
        Thread.sleep(1500);

        for (int i=page; i>0; i--) {

            jse.executeScript("go_Page("+ i +");");
            Thread.sleep(1500);

            for(int j=1; j<16; j++) {

                try {

                    WebElement titleXpath = driver.findElement(By.xpath("//*[@id=\"contentsList\"]/div[2]/table/tbody/tr["+ j +"]/td[2]/a"));
                    WebElement dateXpath = driver.findElement(By.xpath("//*[@id=\"contentsList\"]/div[2]/table/tbody/tr[3]/td[4]"));

                    String baseUrl = "https://www.kiat.or.kr/front/board/boardContentsView.do?contents_id=";
                    String urlIndex = titleXpath.getAttribute("href").replaceAll("javascript:contentsView","").replaceAll("\\(","").replaceAll("\\)","").replaceAll("'","");

                    String title = titleXpath.getText();
                    String bodyurl = baseUrl + urlIndex;
                    String endtime = dateXpath.getText().replaceAll(" ","");

                    ContentsVo vo = new ContentsVo();
                    vo.setTargetname("한국산업기술진흥원");
                    vo.setTargetnamecode("임의코드");
                    vo.setTargettype("-");
                    vo.setTargettypecode("-");
                    vo.setTargetcost("-");
                    vo.setLoccode("C02");
                    vo.setTitle(title);
                    vo.setBodyurl(bodyurl);
                    vo.setEndTime(endtime);

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
