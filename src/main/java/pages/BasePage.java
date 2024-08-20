package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * The `BasePage` class serves as the base class for all page objects in the Page Object Model (POM) design pattern.
 * It provides common functionality such as managing the WebDriver instance and WebDriverWait for explicit waits.
 * All specific page classes should extend this class to inherit the shared behavior.
 */
public abstract class BasePage {
    /**
     * The WebDriver instance used to control the browser.
     */
    protected WebDriver driver;

    /**
     * The WebDriverWait instance used to wait for certain conditions (like element visibility) before proceeding.
     */
    protected WebDriverWait wait;

    /**
     * Constructor to initialize the `BasePage` with the WebDriver instance.
     * It also sets up an explicit wait (`WebDriverWait`) with a default timeout of 15 seconds.
     *
     * @param driver The WebDriver instance passed to control the browser.
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * Provides the base URL for the Trello application.
     * This method can be overridden by subclasses if they need to return a different base URL.
     *
     * @return A string representing the base URL of the Trello application.
     */
    public String getBaseUrl() {
        return "https://trello.com";
    }
}
