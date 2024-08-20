package org.example;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * TrelloAutomation is a Java class that automates interactions with the Trello web application.
 * This class provides functionality to perform various operations on Trello, such as logging in,
 * creating boards and lists, adding cards, moving cards between lists, and closing/deleting boards.

 * The automation is implemented using Selenium WebDriver with Chrome browser. It utilizes explicit
 * waits and retry mechanisms to handle potential timing issues and improve reliability.

 * This class serves as the first iteration for Task 2, providing a foundation for Trello automation
 * that can be extended and refined in future iterations.
 */
public class TrelloAutomation {
    private static final Logger logger = LoggerFactory.getLogger(TrelloAutomation.class);

    private WebDriver driver;
    private WebDriverWait wait;
    private final String baseUrl = "https://trello.com";

    /**
     * Constructs a new TrelloAutomation instance.
     * Initializes the WebDriver and WebDriverWait objects.
     */
    public TrelloAutomation() {
        logger.info("Initializing TrelloAutomation");
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\uwx1322333\\driveroptions\\chrome\\chromedriver.exe");
        this.driver = new ChromeDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // Increased wait time
        this.driver.manage().window().maximize();
        logger.info("WebDriver initialized and maximized");
    }

    /**
     * Logs into Trello using the provided credentials.
     *
     * @param email The email address for login.
     * @param password The password for login.
     */
    public void login(String email, String password) {
        logger.info("Navigating to Trello login page");
        driver.get(baseUrl + "/login");

        logger.info("Entering username: {}", email);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys(email);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))).click();

        logger.info("Entering password");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password"))).sendKeys(password);
        driver.findElement(By.id("login-submit")).click();
        logger.info("Logged in successfully");
    }

    /**
     * Creates a new Trello board with the specified name.
     *
     * @param boardName The name of the board to create.
     */
    public void createBoard(String boardName) {
        logger.info("Creating a board named '{}'", boardName);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[data-testid='header-create-menu-button']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class,'gJDsPins_eYkBM')]//span[contains(text(), 'Create board')]"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[data-testid='create-board-title-input']"))).sendKeys(boardName);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[data-testid='create-board-submit-button']"))).click();
        logger.info("Board '{}' created successfully", boardName);
    }


    /**
     * Creates a new list in the current board with the specified name.
     *
     * @param listName The name of the list to create.
     */
    public void createList(String listName) {
        try {
            logger.info("Attempting to create list: {}", listName);

            // Wait for the page to be in a stable state
            wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete';"));

            // Try to find the "Add another list" button
            WebElement addListButton = null;
            try {
                addListButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("button[data-testid='list-composer-button']")));
            } catch (TimeoutException e) {
                logger.error("Could not find 'Add another list' button. Trying alternative method.");
                // If the button is not found, try to add the first list
                addListButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("button[data-testid='create-list-button']")));
            }

            if (addListButton == null) {
                throw new NoSuchElementException("Could not find any button to add a list");
            }

            // Click the button using JavaScript
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addListButton);
            Thread.sleep(1000); // Wait for 1 second after clicking the button

            // Wait for the input field to be visible and interactable using the class name
            WebElement listNameInput = wait.until(ExpectedConditions.elementToBeClickable(
                    By.className("oe8RymzptORQ7h")));
            listNameInput.clear();  // Clear any existing text
            listNameInput.sendKeys(listName);

            // Wait for the submit button to be clickable
            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[data-testid='list-composer-add-list-button']")));
            submitButton.click();

            // Wait for the new list to appear in the DOM
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//h2[contains(text(), '" + listName + "')]")));

            logger.info("List '{}' created successfully.", listName);
        } catch (Exception e) {
            logger.error("Failed to create list '{}'. Error: {}", listName, e.getMessage());
            e.printStackTrace();  // This will print the full stack trace for debugging

            // Capture and log the page source
            logger.error("Page source at point of failure: {}", driver.getPageSource());

            // Capture and save a screenshot
            try {
                File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                FileUtils.copyFile(screenshot, new File("error_screenshot_" + System.currentTimeMillis() + ".png"));
                logger.info("Screenshot saved");
            } catch (IOException ioe) {
                logger.error("Failed to capture screenshot", ioe);
            }
        }
    }

    /**
     * Creates a new card in the specified list with the given name.
     *
     * @param listName The name of the list where the card should be created.
     * @param cardName The name of the card to create.
     */
    public void createCard(String listName, String cardName) {
        logger.info("Creating card '{}' in list '{}'", cardName, listName);
        try {
            WebElement listContainer = waitAndRetry(() -> findListContainer(listName));

            // Try to find the "Add a card" button or the textarea
            WebElement addCardElement = findAddCardElementInList(listContainer);

            if (addCardElement.getTagName().equals("button")) {
                clickUsingJavaScript(addCardElement);
                addCardElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("textarea[data-testid='list-card-composer-textarea']")));
            }

            addCardElement.clear();
            addCardElement.sendKeys(cardName);

            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[data-testid='list-card-composer-add-card-button']")));
            clickUsingJavaScript(submitButton);

            // Wait for the card to appear in the list
            //  wait.until(ExpectedConditions.visibilityOfElementLocated(
            //        By.xpath("//span[contains(text(), '" + cardName + "')]/ancestor::a[contains(@class, 'list-card')]")));

            logger.info("Card '{}' created successfully in list '{}'", cardName, listName);
            Thread.sleep(1000);
        } catch (Exception e) {
            logger.error("Failed to create card '{}' in list '{}'. Error: {}", cardName, listName, e.getMessage());
        }
    }

    /**
     * Finds the "Add a card" element within a list container.
     *
     * @param listContainer The WebElement representing the list container.
     * @return The WebElement for adding a new card.
     * @throws NoSuchElementException if the element is not found.
     */
    private WebElement findAddCardElementInList(WebElement listContainer) {
        List<WebElement> elements = listContainer.findElements(By.cssSelector("button[data-testid='list-add-card-button'], textarea[data-testid='list-card-composer-textarea']"));
        if (elements.isEmpty()) {
            throw new NoSuchElementException("Could not find 'Add a card' button or textarea in the list");
        }
        return elements.get(0);
    }


    /**
     * Finds the container element for a list with the specified name.
     *
     * @param listName The name of the list to find.
     * @return The WebElement representing the list container.
     */
    private WebElement findListContainer(String listName) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[@data-testid='list-name' and text()='" + listName + "']/ancestor::div[@data-testid='list']")
        ));
    }


    /**
     * Clicks an element using JavaScript.
     *
     * @param element The WebElement to click.
     */
    private void clickUsingJavaScript(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /**
     * Attempts to perform an action with retries.
     *
     * @param action The action to perform, wrapped in a Callable.
     * @param <T> The return type of the action.
     * @return The result of the action.
     * @throws Exception if the action fails after all retries.
     */
    private <T> T waitAndRetry(Callable<T> action) throws Exception {
        Exception lastException = null;
        for (int i = 0; i < 3; i++) {
            try {
                return action.call();
            } catch (Exception e) {
                lastException = e;
                Thread.sleep(1000);
            }
        }
        throw lastException;
    }

    /**
     * Moves a card from its current list to a specified destination list.
     *
     * @param cardName The name of the card to move.
     * @param toListName The name of the destination list.
     */
    public void moveCard(String cardName, String toListName) {
        // Step 1: Click on the specific card to open its details
        WebElement card = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@data-testid='card-name' and text()='" + cardName + "']")));
        card.click();

        // Step 2: Click the 'Move' button within the card detail view
        WebElement moveButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@class, 'js-move-card') and @title='Move']")));
        moveButton.click();

        // Step 3: Wait for the Move Modal to appear and select the destination list
        WebElement listDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@data-testid='move-card-popover-select-list-destination']//input")));
        listDropdown.click();
        listDropdown.sendKeys(toListName);
        listDropdown.sendKeys(org.openqa.selenium.Keys.ENTER);

        // Step 4: Confirm the move by clicking the "Move" button in the modal
        WebElement confirmMoveButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@data-testid='move-card-popover-move-button']")));
        confirmMoveButton.click();

        // Step 5: Close the card detail window using the ESC key
        // Sends the ESC key to close the modal
        WebElement body = driver.findElement(By.tagName("body"));
        body.sendKeys(Keys.ESCAPE);

    }

    /**
     * Closes the current board.
     */
    public void closeBoard() {
        // Step 1: Click on the three dots (Overflow Menu) to open the board options
        WebElement overflowMenuButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id='content']/div/div/div[1]/div[1]/div/span[2]/button[2]/span/span")));
        overflowMenuButton.click();

        // Step 2: Click on the "Close board" option
        WebElement closeBoardOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id='content']/div/div/div[2]/div/div/div/div[2]/div/ul/li[17]/a")));
        closeBoardOption.click();

        // Step 3: Confirm the board closure by clicking the "Close" button in the confirmation pop-up
        WebElement confirmCloseButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id='chrome-container']/div[4]/div/div[2]/div/div/div/input")));
        confirmCloseButton.click();

        // Step 4: Optionally, you can add a verification step to ensure the board was closed successfully
        // For example, you might check the URL or a success message
    }

    /**
     * Permanently deletes the current board.
     */
    public void deleteBoard() {
        // Step 1: Click on the "Permanently delete board" button
        WebElement deleteBoardButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id='content']/div/div/div[2]/div/div/div/div[2]/div/ul/div[3]/div/li/button")));
        deleteBoardButton.click();

        // Step 2: Confirm the deletion by clicking the "Delete" button in the confirmation pop-up
        WebElement confirmDeleteButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div[2]/div/section/div/button")));
        confirmDeleteButton.click();

    }

    /**
     * Closes the WebDriver session and releases associated resources.
     */
    public void tearDown() {
        logger.info("Tearing down WebDriver session");
        driver.quit();
        logger.info("WebDriver session ended");
    }


    /**
     * The main method that demonstrates the usage of the TrelloAutomation class.
     * It performs a series of operations on Trello, including:
     * - Logging in
     * - Creating a board
     * - Creating lists
     * - Adding cards to lists
     * - Moving cards between lists
     * - Closing and deleting the board
     *
     * @param args Command-line arguments (not used in this implementation)
     * @throws InterruptedException If a thread is interrupted during sleep
     */
    public static void main(String[] args) throws InterruptedException {
        logger.info("Starting Trello automation");
        TrelloAutomation automation = new TrelloAutomation();

        // Log in to Trello
        automation.login("erik.umutcan@gmail.com", "BetterVN2024.");

        // Create a new board named "VaultN"
        automation.createBoard("VaultN");

        // Create lists in the board
        String[] lists = {"Backlog", "Todo", "Doing", "Testing", "Done"};
        for (String list : lists) {
            automation.createList(list);
            try {
                Thread.sleep(1000);  // Wait for 1 second between list creations
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        automation.createCard("Todo", "Sign up for Trello");
        automation.createCard("Todo", "Get key and token");
        automation.createCard("Todo", "Build a collection");
        automation.createCard("Todo", "Working on Task");
        automation.createCard("Backlog", "UI Automation");
        automation.createCard("Backlog", "Writing Test Scenarios");

        // Wait for 2 seconds before moving cards
        Thread.sleep(2000);

        // Move cards between lists
        automation.moveCard( "Sign up for Trello", "Done");
        automation.moveCard("Get key and token", "Testing");
        automation.moveCard("Build a collection", "Doing");
        automation.moveCard("Working on Task", "Doing");

        // Close and delete the board
        automation.closeBoard();
        automation.deleteBoard();

        // Clean up resources
        automation.tearDown();
        logger.info("Trello automation completed");
    }


}
