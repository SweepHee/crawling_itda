package security.security.Crawling;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class RcdaCrawling implements Crawling {

    public String url = "http://www.rcda.or.kr/2020/news_add/contest.asp?Page=";
    public int page = 500;

    public void setPage (int page) {
        this.page = page;
    }
    @Override
    public void craw() {

        File driverFile = new File("/Users/seungheejeon/Desktop/workspace/2021_09/security/src/main/resources/chromedriver_96");
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

        try {
            for (int i=page; i>0; i--) {
                System.out.println("페이지::" + i);
                driver.get(url + i);

                for(int j=1; j<11; j++) {
                    WebElement titleXpath = driver.findElement(By.xpath("//*[@id=\"contest\"]/div/div[1]/ul/li["+ j +"]/a/div[2]/div[1]/p"));
                    WebElement urlXpath = driver.findElement(By.xpath("//*[@id=\"contest\"]/div/div[1]/ul/li[" + j +"]/a"));
                    String title = titleXpath.getText();
                    String url = urlXpath.getAttribute("href");
                    System.out.println(title + ":: title");
                    System.out.println(url + ":: url");

//                    String index = url.index;
//                    String mybatis_is_index = true;
//                    if (!mybatis_is_index) {
//                        dbinsert
//                    }
                }

                Thread.sleep(500);
            }



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
            service.stop();
        }

    }

}
