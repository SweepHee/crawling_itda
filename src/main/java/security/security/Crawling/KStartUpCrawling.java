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
public class KStartUpCrawling implements Crawling {

    @Autowired
    ContentsMapper contentsMapper;

    @Autowired
    Environment environment;

    /*
    * 제대로 안됨
    * 쿼리스트링 searchPostSn, searchPrefixCode 만 가져오면 됨
    * 게시글 리스트에서 javascript:itemSelect함수에서 가져오면 된다
    * https://www.k-startup.go.kr/common/announcement/announcementDetail.do?searchPostSn=142665&searchPrefixCode=BOARD_701_001
    * 페이징이 아니고 더보기 버튼. 버튼 로딩하는 로직 필요함
    * */

    private String url = "https://www.k-startup.go.kr/common/announcement/announcementList.do?searchPrefixCode=BOARD_701_001&pageIndex=";
    private int page = 7;

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

        ContentsVo contentsVo = new ContentsVo();
        contentsVo.setTitle("K-Startup");
        contentsVo.setUrl("https://www.k-startup.go.kr/");
        contentsVo.setLocation("C02");
        contentsVo.setActiveYn("Y");
        contentsVo.setErrorYn("N");

        List<ContentsVo> contentsVos = new ArrayList<>();

        try {
            for (int i=page; i>0; i--) {
                System.out.println("페이지::" + i);
                driver.get(url + i);

                for(int j=1; j<16; j++) {

                    WebElement titleXpath = driver.findElement(By.xpath("//*[@id='bizPbancList']/ul/li["+ j +"]/div/div[1]/div[2]/a/div/p"));
                    WebElement urlXpath = driver.findElement(By.xpath("//*[@id='bizPbancList']/ul/li["+ j +"]/div/div[1]/div[2]/a"));
                    WebElement targetXpath = driver.findElement(By.xpath("//*[@id='bizPbancList']/ul/li["+ j +"]/div/div[1]/div[1]/span[1]"));
                    WebElement dateXpath = driver.findElement(By.xpath("//*[@id='bizPbancList']/ul/li["+ j +"]/div/div[1]/div[3]/span[3]"));


                    String title = titleXpath.getText();
                    String url = urlXpath.getAttribute("href");
                    String targettype = targetXpath.getText();
                    String endtime = dateXpath.getText().replaceAll("마감일자 ","");
                    Pattern p = Pattern.compile("(['\"])[^'\"]*\\1");
                    System.out.println(title + ":: title");
                    Matcher m = p.matcher(url);
                    ArrayList<String> pattern = new ArrayList<String>();

                    while (m.find()) {
                        pattern.add(m.group());
                    }

                    String bodyUrl = "https://www.k-startup.go.kr/common/announcement/announcementDetail.do?searchPostSn=" + pattern.get(1).replace("'", "") +"&searchPrefixCode=" + pattern.get(0).replace("'", "");

                    ContentsVo vo = new ContentsVo();
                    vo.setTargetname("K-Startup");
                    vo.setTargetnamecode("임의코드");
                    vo.setTargettype(targettype);
                    vo.setTargettypecode(targettype);
                    vo.setTargetcost("-");
                    vo.setLoccode("C02");
                    vo.setTitle(title);
                    vo.setBodyurl(bodyUrl);
                    vo.setEndTime(endtime);
                    System.out.println(vo.toString());
                    HashMap<String, String> params = new HashMap<>();
                    params.put("bodyurl", bodyUrl);
                    boolean isUrl = contentsMapper.isUrl(params);
                    if (!isUrl) {
                        contentsVos.add(vo);
                    }

                }

                Thread.sleep(500);
            }

        } catch (Exception e) {
            e.printStackTrace();
            contentsMapper.createMaster(contentsVo);
            System.out.println(e.getMessage());
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
