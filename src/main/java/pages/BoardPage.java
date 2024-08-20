package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * The `BoardPage` class represents the Trello Board page and provides methods to interact with it.
 * It allows the creation, closure, and deletion of Trello boards using the Page Object Model (POM) design pattern.
 */
public class BoardPage extends BasePage {

    // Selectors for creating a new board
    private final By createMenuButton = By.cssSelector("button[data-testid='header-create-menu-button']");
    private final By createBoardOption = By.xpath("//button[contains(@class,'gJDsPins_eYkBM')]//span[contains(text(), 'Create board')]");
    private final By boardNameInput = By.cssSelector("input[data-testid='create-board-title-input']");
    private final By createBoardSubmitButton = By.cssSelector("button[data-testid='create-board-submit-button']");

    // Selectors for closing and deleting a board
    private final By overflowMenuButton = By.xpath("//*[@id='content']/div/div/div[1]/div[1]/div/span[2]/button[2]/span/span");
    private final By closeBoardOption = By.xpath("//*[@id='content']/div/div/div[2]/div/div/div/div[2]/div/ul/li[17]/a");
    private final By confirmCloseButton = By.xpath("//*[@id='chrome-container']/div[4]/div/div[2]/div/div/div/input");
    private final By deleteBoardButton = By.xpath("//*[@id='content']/div/div/div[2]/div/div/div/div[2]/div/ul/div[3]/div/li/button");
    private final By confirmDeleteButton = By.xpath("/html/body/div[2]/div/section/div/button");

    /**
     * Constructor for the `BoardPage` class. Initializes the WebDriver and WebDriverWait inherited from `BasePage`.
     *
     * @param driver The WebDriver instance used to interact with the browser.
     */
    public BoardPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Creates a new Trello board with the specified name.
     *
     * @param boardName The name of the board to be created.
     */
    public void createBoard(String boardName) {
        WebElement menuButton = wait.until(ExpectedConditions.elementToBeClickable(createMenuButton));
        clickUsingJavaScript(menuButton);

        WebElement boardOption = wait.until(ExpectedConditions.elementToBeClickable(createBoardOption));
        clickUsingJavaScript(boardOption);

        WebElement boardNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(boardNameInput));
        boardNameField.sendKeys(boardName);

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(createBoardSubmitButton));
        clickUsingJavaScript(submitButton);
    }

    /**
     * Closes the currently open Trello board.
     * This method navigates through the board's menu to close it.
     */
    public void closeBoard() {
        WebElement overflowMenu = wait.until(ExpectedConditions.elementToBeClickable(overflowMenuButton));
        clickUsingJavaScript(overflowMenu);

        WebElement closeOption = wait.until(ExpectedConditions.elementToBeClickable(closeBoardOption));
        clickUsingJavaScript(closeOption);

        WebElement confirmClose = wait.until(ExpectedConditions.elementToBeClickable(confirmCloseButton));
        clickUsingJavaScript(confirmClose);
    }

    /**
     * Permanently deletes the currently open Trello board.
     * This method should only be used after a board has been closed.
     */
    public void deleteBoard() {
        WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(deleteBoardButton));
        clickUsingJavaScript(deleteButton);

        WebElement confirmDelete = wait.until(ExpectedConditions.elementToBeClickable(confirmDeleteButton));
        clickUsingJavaScript(confirmDelete);
    }

    /**
     * Utility method to click on a WebElement using JavaScript.
     * This is useful when standard click actions may not work due to overlapping elements or other issues.
     *
     * @param element The WebElement to be clicked.
     */
    private void clickUsingJavaScript(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }
}
