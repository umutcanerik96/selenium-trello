
import BoardPage from '../pages/boardPage';



Cypress.Commands.add('login', () => {
    // Load credentials from fixture file
    cy.fixture('credentials').then((credentials) => {
      cy.visit('https://trello.com/');
      cy.get('a').contains('Log in').click();
  
      // Handle the login through Atlassian's login page and pass credentials using `args`
      cy.origin('https://id.atlassian.com', { args: { credentials } }, ({ credentials }) => {
        cy.get("#username").type(credentials.email);
        cy.get('button').contains('Continue').click();
        cy.get('#password').type(credentials.password);
        cy.get('button').contains('Log in').click();
      });
  
      // Wait for successful login and redirection
      cy.url().should('include', '/boards');
    });
  });


  
/**
 *  Retrieves the names of all cards present in a specified list.
 *  This command selects all card elements within the given list and extracts their text content.
 *  It is useful for verifying card names in a list, which can be helpful for assertions or further operations.
 * 
 *  @param {string} listName - The name of the list to get card names from.
 *  @returns {Array<string>} - An array of card names (text content of the card elements).
 */

Cypress.Commands.add('cardNamesOfTheList', (listName) => {
  cy.get('li')
    .find('h2')
    .contains(listName)
    .closest('li')
    .find('div')
    .find('a')
    .then(($links) => {
      const cardNames = $links.toArray().map(link => Cypress.$(link).text());
      return cardNames;
    });
});


/**
 * Moves a card from one list to another on the board.
 * 
 * @param {string} fromListName - The name of the list from which the card will be moved.
 * @param {string} cardName - The name of the card to be moved.
 * @param {string} toListName - The name of the list to which the card will be moved.
 * 
 * This command locates the specified card in the source list, opens the move dialog,
 * selects the target list, and confirms the move. It then closes the move dialog.
 */
Cypress.Commands.add('moveCard', (fromListName, cardName, toListName) => {
  const boardPage = new BoardPage();
  // Locate the source list (fromListName)
  cy.get('li')
    .find('h2')
    .contains(fromListName)
    .closest('li')
    .find('div')
    .find('a').contains(cardName).click()

  // Open the move card dialog
  boardPage.getMoveButton().click();

  // Select the target list (toListName) from the dropdown
  boardPage.getListDropDown().click().type(`${toListName}{enter}`);

  // Confirm the move
  boardPage.getMoveButtonInMoveCardPopup().click();

  // Close the edit card popup
  boardPage.getCloseButtonInEditCardPopup().click();
});

/**
 * This code is used for handling uncaught exceptions that occur during test execution.
 * Specifically, it addresses errors that originate from the application rather than from Cypress itself.
 * 
 *  We are configuring Cypress to ignore a specific type of error message related to the 
 * `ResizeObserver` API. The `ResizeObserver loop completed with undelivered notifications` warning is common
 * in applications and does not typically affect test functionality.
 */
Cypress.on('uncaught:exception', (err, runnable) => {
  // Ignore ResizeObserver related errors
  if (err.message.includes('ResizeObserver loop completed with undelivered notifications')) {
    return false; // Prevent Cypress from failing the test
  }
  // Allow other uncaught exceptions to fail the test
  return true;
});
