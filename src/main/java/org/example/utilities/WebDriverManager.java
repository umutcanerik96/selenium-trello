package org.example.utilities;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WebDriverManager {

    private WebDriver driver;
    private WebDriverWait wait;

    public WebDriver initializeDriver() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\uwx1322333\\driveroptions\\chrome\\chromedriver.exe");
        this.driver = new ChromeDriver();
        this.driver.manage().window().maximize();
        this.wait = new WebDriverWait(driver, Duration.ofMillis(15));
        return driver;
    }

    public void quitDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}
