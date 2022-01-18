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
public class YouthSeoulCrawling implements Crawling {

    @Autowired
    ContentsMapper contentsMapper;

    @Autowired
    Environment environment;

    /*
     * 서울청년정책
     * https://youth.seoul.go.kr/
     *  */

    private String url = "https://youth.seoul.go.kr/site/main/youth/politics/user/list?&pageSize=12&cp=";
    private int page = 1;

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public void craw() {

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

        try {
            for (int i=page; i>0; i--) {

                driver.get(url + i);

                for(int j=1; j<11; j++) {
                    WebElement titleXpath = driver.findElement(By.xpath("//*[@id=\"searchMove\"]/li["+j+"]/div/a"));
                    WebElement endTimeXpath = driver.findElement(By.xpath("//*[@id=\"searchMove\"]/li["+j+"]/div/ul/li[1]"));

                    String title = titleXpath.getText();
                    String endtime = endTimeXpath.getText();

                    String url = titleXpath.getAttribute("onclick");

                    String[] split = url.split(",");
                    String intStr = split[0].replaceAll("[^0-9]", "");


                    Pattern p = Pattern.compile("\\[(.*?)\\]");
                    Matcher m = p.matcher(title);
                    ArrayList<String> pattern = new ArrayList<String>();

                    while (m.find()) {
                        pattern.add(m.group());
                    }

                    String bodyurl = "https://youth.seoul.go.kr/site/main/youth/politics/user/detail/" + intStr;
                    String targettype = pattern.get(0).replaceAll("\\[", "").replaceAll("\\]", "");

                    ContentsVo vo = new ContentsVo();
                    vo.setTargetname("서울청년포털");
                    vo.setTargetnamecode("임의코드");
                    vo.setTargettype(targettype);
                    vo.setTargettypecode(targettype);
                    vo.setTargetcost("-");
                    vo.setLoccode("C02");
                    vo.setTitle(title);
                    vo.setBodyurl(bodyurl);
                    vo.setEndTime(endtime);

                    HashMap<String, String> params = new HashMap<>();
                    params.put("bodyurl", bodyurl);
                    boolean isUrl = contentsMapper.isUrl(params);
                    System.out.println("이미 수집된 URL입니다::" + isUrl);
                    if (!isUrl) {
                        contentsVos.add(vo);
                    }

                }

                Thread.sleep(500);
            }

            /* 빈 리스트가 아니면 크레이트 */
            if (!contentsVos.isEmpty()) {
                contentsMapper.create(contentsVos);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
            service.stop();
        }

    }


}
