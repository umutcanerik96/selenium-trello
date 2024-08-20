package org.example;

import pages.BoardPage;
import pages.CardPage;
import pages.ListPage;
import pages.LoginPage;
import org.example.utilities.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * This class contains automated tests for Trello functionality.
 * It uses Selenium WebDriver to interact with the Trello web application
 * and performs various operations such as login, board creation,
 * list creation, card creation, and card movement.
 */
public class TrelloAutomationTest {

    // WebDriver instance to control the browser
    private WebDriver driver;

    // WebDriverManager to handle driver initialization and cleanup
    private WebDriverManager webDriverManager;

    // Page objects for different Trello pages
    private LoginPage loginPage;
    private BoardPage boardPage;
    private ListPage listPage;
    private CardPage cardPage;

    /**
     * Sets up the test environment before running the tests.
     * Initializes the WebDriver and page objects.
     */
    @BeforeClass
    public void setup() {
        // Initialize WebDriverManager
        webDriverManager = new WebDriverManager();

        // Initialize WebDriver
        driver = webDriverManager.initializeDriver();

        // Initialize page objects
        loginPage = new LoginPage(driver);
        boardPage = new BoardPage(driver);
        listPage = new ListPage(driver);
        cardPage = new CardPage(driver);
    }

    /**
     * Main test method that performs the Trello automation workflow.
     *
     * @throws InterruptedException if the thread is interrupted during sleep
     */
    @Test
    public void testTrelloAutomation() throws InterruptedException {
        // Login to Trello
        loginPage.login("erik.umutcan@gmail.com", "BetterVN2024.");

        // Create a new board named "VaultN"
        boardPage.createBoard("VaultN");

        // Create lists
        String[] lists = {"Backlog", "Todo", "Doing", "Testing", "Done"};
        for (String list : lists) {
            listPage.createList(list);
            Thread.sleep(1000);  // Wait between list creations
        }

        // Create cards in different lists
        cardPage.createCard("Todo", "Sign up for Trello");
        cardPage.createCard("Todo", "Get key and token");
        cardPage.createCard("Todo", "Build a collection");
        cardPage.createCard("Todo", "Working on Task");
        cardPage.createCard("Backlog", "UI Automation");
        cardPage.createCard("Backlog", "Writing Test Scenarios");

        // Move cards between lists
        listPage.moveCard("Sign up for Trello", "Done");
        listPage.moveCard("Get key and token", "Testing");
        listPage.moveCard("Build a collection", "Doing");
        listPage.moveCard("Working on Task", "Doing");

        // Wait for 2 seconds to observe the changes
        Thread.sleep(2000);

        // Close and delete the board
        boardPage.closeBoard();
        boardPage.deleteBoard();
    }

    /**
     * Cleans up resources after the tests have run.
     * Quits the WebDriver.
     */
    @AfterClass
    public void teardown() {
        // Quit the WebDriver
        webDriverManager.quitDriver();
    }
}