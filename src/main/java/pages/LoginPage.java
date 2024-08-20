package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {

    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By loginSubmitButton = By.id("login-submit");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void login(String email, String password) {
        driver.get(getBaseUrl() + "/login");

        // Wait for the username field to be visible and clickable
        WebElement usernameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
        scrollIntoView(usernameElement);
        usernameElement.sendKeys(email);

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))).click();

        // Wait for the password field to be visible and clickable
        WebElement passwordElement = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField));
        scrollIntoView(passwordElement);
        passwordElement.sendKeys(password);

        driver.findElement(loginSubmitButton).click();
    }

    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }
}
