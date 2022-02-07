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
public class SeoulDongbuCrawling implements Crawling {

    @Autowired
    ContentsMapper contentsMapper;

    @Autowired
    Environment environment;

    /*
     * 동부여성발전센터
     * https://dongbu.seoulwomanup.or.kr/
     *  */

    private String url = "https://dongbu.seoulwomanup.or.kr/dongbu/common/bbs/selectPageListBBS.do?bbs_seq=129547&bbs_code=G0603&bbs_type_code=10&currentPage=";
    private int page = 1;

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
        contentsVo.setTitle("동부여성발전센터");
        contentsVo.setUrl("https://dongbu.seoulwomanup.or.kr");
        contentsVo.setLocation("C02");
        contentsVo.setActiveYn("Y");
        contentsVo.setErrorYn("N");
        List<ContentsVo> contentsVos = new ArrayList<>();


        for (int i=page; i>0; i--) {

            driver.get(url + i);
            Thread.sleep(1000);
            for(int j=1; j<4; j++) {
                    try {

                        WebElement titleXpath = driver.findElement(By.xpath("//*[@id='aform']/table/tbody/tr["+ j +"]/td[2]/a"));

                        Pattern typePattern = Pattern.compile("\\[(.*?)\\]"); // 대괄호안에 문자 뽑기
                        Matcher typeMatcher = typePattern.matcher(titleXpath.getText());
                        ArrayList<String> typePatternArray = new ArrayList<String>();

                        while (typeMatcher.find()) {
                            typePatternArray.add(typeMatcher.group());
                        }

                        ContentsVo vo = new ContentsVo();

                        String title = titleXpath.getText();
                        String url = titleXpath.getAttribute("onclick").replaceAll("fnSelect","").replaceAll("\\(","").replaceAll("\\)","").replaceAll("'","");
                        String[] urlTemp = url.split(",");
                        String bodyurl = "https://dongbu.seoulwomanup.or.kr/dongbu/common/bbs/selectBBS.do?bbs_code=G0603&bbs_type_code=10&bbs_seq=" + urlTemp[0];

                        //타입코드 없을경우 - 처리
                        if(!typePatternArray.isEmpty()){
                            String targettype = typePatternArray.get(0).replaceAll("\\[", "").replaceAll("\\]", "");
                            vo.setTargettype(targettype);
                            vo.setTargettypecode(targettype);
                        }else {
                            vo.setTargettype("-");
                            vo.setTargettypecode("-");
                        }

                        vo.setTargetname("동부여성발전센터");
                        vo.setTargetnamecode("임의코드");
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
