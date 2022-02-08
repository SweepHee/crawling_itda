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
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SeoulSbdcCrawling implements Crawling {

    @Autowired
    ContentsMapper contentsMapper;

    @Autowired
    Environment environment;

    /*
     * 서울시 자영업지원센터
     * https://www.seoulsbdc.or.kr/
     * 게시글 페이지가 POST로 되어 있어서 자바스크립트 함수 실행 처리를 했음
     *  */

    private String url = "https://www.seoulsbdc.or.kr/cs/businessSearch.do";
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
        contentsVo.setTitle("서울시자영업지원센터");
        contentsVo.setUrl("https://www.seoulsbdc.or.kr/");
        contentsVo.setLocation("C02");
        contentsVo.setActiveYn("Y");
        contentsVo.setErrorYn("N");

        List<ContentsVo> contentsVos = new ArrayList<>();

        driver.get(url);
        Thread.sleep(1500);

        for (int i=page; i>0; i--) {

            jse.executeScript("movePage('"+ i +"');");
            Thread.sleep(1500);

            for(int j=1; j<11; j++) {

                try {

//                    WebElement titleXpath = driver.findElement(By.xpath("//*[@id=\"container\"]/div/div/div["+j+"]/div[2]/a/text"));
                    String baseUrl = "https://www.seoulsbdc.or.kr/cs/supportBusinessDetail.do?mseq=";

                    WebElement bodyUrlXpath = driver.findElement(By.xpath("//*[@id=\"container\"]/div/div/div["+j+"]/div[2]/a"));
                    String onclickFn = bodyUrlXpath.getAttribute("onclick");

                    Pattern p = Pattern.compile("'(.*?)'");
                    Matcher m = p.matcher(onclickFn);
                    ArrayList<String> pattern = new ArrayList<String>();

                    while (m.find()) {
                        pattern.add(m.group());
                    }

                    String mseq = pattern.get(0).replaceAll("'", "");
                    String url = baseUrl + mseq;

                    String targettype = "";

                    WebElement endTimeXpath = driver.findElement(By.xpath("//*[@id=\"container\"]/div/div/div["+j+"]/div[3]"));

                    String title = bodyUrlXpath.getText();
                    String endTime = endTimeXpath.getText();

                    ContentsVo vo = new ContentsVo();
                    vo.setTargetname("서울시자영업지원센터");
                    vo.setTargetnamecode("임의코드");
                    vo.setTargettype(targettype);
                    vo.setTargettypecode(targettype);
                    vo.setTargetcost("-");
                    vo.setLoccode("C02");
                    vo.setTitle(title);
                    vo.setBodyurl(url);
                    vo.setEndTime(endTime);

                    System.out.println(vo.toString());

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
