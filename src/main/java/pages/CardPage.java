package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.concurrent.Callable;

public class CardPage extends BasePage {

    public CardPage(WebDriver driver) {
        super(driver);
    }

    public void createCard(String listName, String cardName) {
        try {
            WebElement listContainer = waitAndRetry(() -> findListContainer(listName));

            // Find the "Add a card" button or the textarea
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
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//span[contains(text(), '" + cardName + "')]/ancestor::a[contains(@class, 'list-card')]")));

            System.out.println("Card '" + cardName + "' created successfully in list '" + listName + "'");
        } catch (Exception e) {
            System.err.println("Failed to create card '" + cardName + "' in list '" + listName + "'. Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private WebElement findAddCardElementInList(WebElement listContainer) {
        List<WebElement> elements = listContainer.findElements(By.cssSelector("button[data-testid='list-add-card-button'], textarea[data-testid='list-card-composer-textarea']"));
        if (elements.isEmpty()) {
            throw new NoSuchElementException("Could not find 'Add a card' button or textarea in the list");
        }
        return elements.get(0);
    }

    private WebElement findListContainer(String listName) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[@data-testid='list-name' and text()='" + listName + "']/ancestor::div[@data-testid='list']")
        ));
    }

    private void clickUsingJavaScript(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

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
}
