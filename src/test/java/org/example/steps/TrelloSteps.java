package org.example.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.BoardPage;
import pages.CardPage;
import pages.ListPage;
import pages.LoginPage;
import org.example.utilities.WebDriverManager;
import org.openqa.selenium.WebDriver;

public class TrelloSteps {

    private WebDriver driver;
    private WebDriverManager webDriverManager;
    private LoginPage loginPage;
    private BoardPage boardPage;
    private ListPage listPage;
    private CardPage cardPage;

    @Before
    public void setup() {
        webDriverManager = new WebDriverManager();
        driver = webDriverManager.initializeDriver();
        loginPage = new LoginPage(driver);
        boardPage = new BoardPage(driver);
        listPage = new ListPage(driver);
        cardPage = new CardPage(driver);
    }

    @Given("I am logged in to Trello")
    public void i_am_logged_in_to_trello() {
        loginPage.login("erik.umutcan@gmail.com", "BetterVN2024.");
    }

    @When("I create a board named {string}")
    public void i_create_a_board_named(String boardName) {
        boardPage.createBoard(boardName);
    }

    @When("I add lists {string}, {string}, {string}, {string}, {string} to the board")
    public void i_add_lists_to_the_board(String list1, String list2, String list3, String list4, String list5) throws InterruptedException {
        String[] lists = {list1, list2, list3, list4, list5};
        for (String list : lists) {
            listPage.createList(list);
            Thread.sleep(2000);  // Wait between list creations (2 seconds for better stability)
        }
    }

    @When("I create cards in {string} and {string}")
    public void i_create_cards_in_and(String list1, String list2) {
        cardPage.createCard(list1, "Sign up for Trello");
        cardPage.createCard(list1, "Get key and token");
        cardPage.createCard(list1, "Build a collection");
        cardPage.createCard(list1, "Working on Task");
        cardPage.createCard(list2, "UI Automation");
        cardPage.createCard(list2, "Writing Test Scenarios");
    }

    @Then("I move the cards to their appropriate lists")
    public void i_move_the_cards_to_their_appropriate_lists() {
        listPage.moveCard("Sign up for Trello", "Done");
        listPage.moveCard("Get key and token", "Testing");
        listPage.moveCard("Build a collection", "Doing");
        listPage.moveCard("Working on Task", "Doing");
    }

    @Then("I close and delete the board")
    public void i_close_and_delete_the_board() {
        boardPage.closeBoard();
        boardPage.deleteBoard();
    }

    @After
    public void teardown() {
        webDriverManager.quitDriver();
    }
}
