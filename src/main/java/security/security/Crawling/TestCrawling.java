package security.security.Crawling;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;


@Component
public class TestCrawling {


    private final String url = "http://www.rcda.or.kr/2020/news_add/contest.asp?Page=1";

    public void craw() {

        File driverFile = new File("/Users/seungheejeon/Desktop/workspace/2021_09/security/src/main/resources/chromedriver");
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
            driver.get(url);
            Thread.sleep(5000);
            WebElement naverBottom = driver.findElement(By.className("partner_box_wrap"));

            Actions actionProvider = new Actions(driver);
            actionProvider.moveToElement(naverBottom).build().perform();
            Thread.sleep(2000);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
            service.stop();
        }

    }



}
