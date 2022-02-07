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
public class SeoulRcdaCrawling implements Crawling {

    @Autowired
    ContentsMapper contentsMapper;

    @Autowired
    Environment environment;

    /*
     * 지역문화진흥원
     * http://www.rcda.or.kr/2020/
     *  */

    private String url = "http://www.rcda.or.kr/2020/news_add/contest.asp?b_code=15&Page=";
    private int page = 2;

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
        contentsVo.setTitle("지역문화진흥원");
        contentsVo.setUrl("http://www.rcda.or.kr/2020/");
        contentsVo.setLocation("C02");
        contentsVo.setActiveYn("Y");
        contentsVo.setErrorYn("N");
        List<ContentsVo> contentsVos = new ArrayList<>();


        for (int i=page; i>0; i--) {

            driver.get(url + i);
            Thread.sleep(1000);
            for(int j=1; j<11; j++) {
                    try {

                        WebElement titleXpath = driver.findElement(By.xpath("/html/body/div/div[2]/div[2]/div[1]/div/div/div/div[1]/ul/li["+ j +"]/a/div[2]/div[1]/p"));
                        WebElement urlXpath = driver.findElement(By.xpath("/html/body/div/div[2]/div[2]/div[1]/div/div/div/div[1]/ul/li["+ j +"]/a"));
                        WebElement dateXpath = driver.findElement(By.xpath("/html/body/div/div[2]/div[2]/div[1]/div/div/div/div[1]/ul/li["+ j +"]/a/div[2]/ul/li[1]"));

                        Pattern typePattern = Pattern.compile("\\[(.*?)\\]"); // 대괄호안에 문자 뽑기
                        Matcher typeMatcher = typePattern.matcher(titleXpath.getText());
                        ArrayList<String> typePatternArray = new ArrayList<String>();

                        while (typeMatcher.find()) {
                            typePatternArray.add(typeMatcher.group());
                        }

                        ContentsVo vo = new ContentsVo();

                        String title = titleXpath.getText();
                        String bodyurl = urlXpath.getAttribute("href");
                        String endtiem = dateXpath.getText().replaceAll("접수기간 : ","");

                        //타입코드 없을경우 - 처리
                        if(!typePatternArray.isEmpty()){
                            String targettype = typePatternArray.get(0).replaceAll("\\[", "").replaceAll("\\]", "");
                            vo.setTargettype(targettype);
                            vo.setTargettypecode(targettype);
                        }else {
                            vo.setTargettype("-");
                            vo.setTargettypecode("-");
                        }

                        vo.setTargetname("지역문화진흥원");
                        vo.setTargetnamecode("임의코드");
                        vo.setTargetcost("-");
                        vo.setLoccode("C02");
                        vo.setTitle(title);
                        vo.setBodyurl(bodyurl);
                        vo.setEndTime(endtiem);
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
