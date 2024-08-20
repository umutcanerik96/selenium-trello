package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ListPage extends BasePage {

    private final By addListButton = By.cssSelector("button[data-testid='list-composer-button']");
    private final By listNameInput = By.className("oe8RymzptORQ7h");
    private final By addListSubmitButton = By.cssSelector("button[data-testid='list-composer-add-list-button']");

    public ListPage(WebDriver driver) {
        super(driver);
    }

    public void createList(String listName) {
        try {
            wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete';"));

            WebElement addButton = wait.until(ExpectedConditions.presenceOfElementLocated(addListButton));
            clickUsingJavaScript(addButton);

            WebElement listNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(listNameInput));
            listNameField.clear();
            listNameField.sendKeys(listName);

            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(addListSubmitButton));
            clickUsingJavaScript(submitButton);

            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//h2[contains(text(), '" + listName + "')]")));
        } catch (TimeoutException e) {
            System.out.println("Element not found within the time frame: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createCard(String listName, String cardName) {
        try {
            WebElement listContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h2[@data-testid='list-name' and text()='" + listName + "']/ancestor::div[@data-testid='list']")));

            WebElement addCardButton = findAddCardElementInList(listContainer);

            if (addCardButton.getTagName().equals("button")) {
                clickUsingJavaScript(addCardButton);
                addCardButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("textarea[data-testid='list-card-composer-textarea']")));
            }

            addCardButton.clear();
            addCardButton.sendKeys(cardName);

            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[data-testid='list-card-composer-add-card-button']")));
            clickUsingJavaScript(submitButton);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void moveCard(String cardName, String toListName) {
        try {
            WebElement card = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[@data-testid='card-name' and text()='" + cardName + "']")));
            card.click();

            WebElement moveButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(@class, 'js-move-card') and @title='Move']")));
            moveButton.click();

            WebElement listDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[@data-testid='move-card-popover-select-list-destination']//input")));
            listDropdown.click();
            listDropdown.sendKeys(toListName);
            listDropdown.sendKeys(Keys.ENTER);

            WebElement confirmMoveButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@data-testid='move-card-popover-move-button']")));
            confirmMoveButton.click();

            WebElement body = driver.findElement(By.tagName("body"));
            body.sendKeys(Keys.ESCAPE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WebElement findAddCardElementInList(WebElement listContainer) {
        return listContainer.findElements(By.cssSelector("button[data-testid='list-add-card-button'], textarea[data-testid='list-card-composer-textarea']"))
                .stream().findFirst().orElseThrow(() -> new NoSuchElementException("Could not find 'Add a card' button or textarea in the list"));
    }

    private void clickUsingJavaScript(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }
}
