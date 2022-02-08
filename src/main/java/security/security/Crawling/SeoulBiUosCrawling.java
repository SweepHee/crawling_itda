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
public class SeoulBiUosCrawling implements Crawling {

    @Autowired
    ContentsMapper contentsMapper;

    @Autowired
    Environment environment;

    /*
     * 서대문구-서울시립대학교 창원지원센터
     * http://bi.uos.ac.kr/
     *  */

    private String url = "http://bi.uos.ac.kr/em/em_1.php?p_page=";
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
        contentsVo.setTitle("서대문구-서울시립대학교창원지원센터");
        contentsVo.setUrl("http://bi.uos.ac.kr/");
        contentsVo.setLocation("C02");
        contentsVo.setActiveYn("Y");
        contentsVo.setErrorYn("N");

        List<ContentsVo> contentsVos = new ArrayList<>();


        for (int i=page; i>0; i--) {

            driver.get(url + i);

            for(int j=1; j<11; j++) {

                try {

                    WebElement titleXpath = driver.findElement(By.xpath("//*[@id=\"sub_contents_wrpabox\"]/div[2]/form[1]/div/div[2]/table/tbody/tr["+j+"]/td[2]/div/a"));

                    String baseUrl = "http://bi.uos.ac.kr/em/em_1.php?p_page=1&code=b_1&p_mode=view&p_idx=";
                    String title = titleXpath.getText();
                    String url = titleXpath.getAttribute("href");

                    String bodyUrl = baseUrl + url.replaceAll("[^0-9]", "");

                    WebElement targetTypeXpath = driver.findElement(By.xpath("//*[@id=\"sub_contents_wrpabox\"]/div[2]/form[1]/div/div[2]/table/tbody/tr["+j+"]/td[2]/div"));
                    Pattern typePattern = Pattern.compile("\\[(.*?)\\]"); // 대괄호안에 문자 뽑기
                    Matcher typeMatcher = typePattern.matcher(targetTypeXpath.getText());
                    ArrayList<String> typePatternArray = new ArrayList<String>();

                    while (typeMatcher.find()) {
                        typePatternArray.add(typeMatcher.group());
                    }

                    String targettype = typePatternArray.get(0).replaceAll("\\[", "").replaceAll("]", "");

                    ContentsVo vo = new ContentsVo();
                    vo.setTargetname("서대문구-서울시립대학교 창원지원센터");
                    vo.setTargetnamecode("임의코드");
                    vo.setTargettype(targettype);
                    vo.setTargettypecode(targettype);
                    vo.setTargetcost("-");
                    vo.setLoccode("C02");
                    vo.setTitle(title);
                    vo.setBodyurl(bodyUrl);
                    vo.setEndTime("");
                    System.out.println(vo.toString());
                    HashMap<String, String> params = new HashMap<>();
                    params.put("bodyurl", bodyUrl);
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
