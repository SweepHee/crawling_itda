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
public class SeoulSongpaCrawling implements Crawling {

    @Autowired
    ContentsMapper contentsMapper;

    @Autowired
    Environment environment;

    /*
     * 송파구일자리통합지원센터
     * https://www.songpa.go.kr/
     *  */

    private String url = "https://www.songpa.go.kr/job/selectBbsNttList.do?bbsNo=125&&pageUnit=10&key=3452&searchAditfield4=&searchAditfield5=&viewBgnde=&searchAditfield1=&searchAditfield2=&pageIndex=";
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

        List<ContentsVo> contentsVos = new ArrayList<>();

        ContentsVo contentsVo = new ContentsVo();
        contentsVo.setTitle("송파구일자리통합지원센터");
        contentsVo.setUrl("https://www.songpa.go.kr/");
        contentsVo.setLocation("C02");
        contentsVo.setActiveYn("Y");
        contentsVo.setErrorYn("N");

        for (int i=page; i>0; i--) {

            driver.get(url + i);

            for(int j=1; j<11; j++) {

                try {

                    WebElement titleXpath = driver.findElement(By.xpath("//*[@id=\"board\"]/div/table/tbody/tr["+j+"]/td[2]/a"));
                    WebElement targettypeXpath = driver.findElement(By.xpath("//*[@id=\"board\"]/div/table/tbody/tr["+j+"]/td[3]"));

                    String title = titleXpath.getText();
                    String bodyUrl = titleXpath.getAttribute("href");
                    String targettype = targettypeXpath.getText();

                    ContentsVo vo = new ContentsVo();
                    vo.setTargetname("송파구일자리통합지원센터");
                    vo.setTargetnamecode("임의코드");
                    vo.setTargettype(targettype);
                    vo.setTargettypecode(targettype);
                    vo.setTargetcost("-");
                    vo.setLoccode("C02");
                    vo.setTitle(title);
                    vo.setBodyurl(bodyUrl);
                    vo.setEndTime("");
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
