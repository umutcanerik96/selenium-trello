import BoardPage from '../pages/boardPage';

describe('Creating board, adding list', () => {

  beforeEach(() => {
    cy.login();// login is implemented as a custom command
  })

  const boardPage = new BoardPage();
  it('Login and create a board', () => {

    //Creating boards steps
    boardPage.getCreateButton().click();
    boardPage.getCreateABoardButton().click();
    boardPage.getBoardTitleInputBox().type("Board1")
    boardPage.getCreateButtonInPopup().click();

    //assertion of created board "VaultN"
    cy.contains('Board1').should('exist');

  });


  it('Create lists', () => {

    boardPage.getBoardByName("Board1").click();
    boardPage.getAddListButton().click();

    //getting list from fixture file
    cy.fixture('lists.json').then((data) => {
      const lists = data.lists;
      // Now you can use the lists array in your test
      lists.forEach((list) => {
        boardPage.getEnterListNameBox().type(list + "{enter}")
      });
      lists.forEach((list) => {
        cy.contains(list).should('exist');
      });
    });

  });


  it('Create cards in lists within the board', () => {

    boardPage.getBoardByName("Board1").click();

    cy.fixture('cards').then((data) => {
      cy.wrap(data.cards).as('cardList');
    });

    // Ensure the data is loaded
    cy.get('@cardList').then((cards) => {
      let currentListName = '';

      cards.forEach((card) => {
        if (currentListName !== card.listName) {
          // Click "Add a card" button for the new list
          boardPage.getAddCardButtonForList(card.listName).click();
          currentListName = card.listName;
        }
        // Enter the card name and add the car
        boardPage.getCardNameInputBox().type(card.cardName + "{enter}")
      });
    });

    // Access the aliased cardList
    cy.get('@cardList').each((card) => {
      // Fetch card names from the specified list
      cy.cardNamesOfTheList(card.listName).then(cardNames => {
        // Assert that the card name is present in the list
        expect(cardNames).to.include(card.cardName);
      });
    });
  });



  it('Move existing cards to related lists', () => {

    boardPage.getBoardByName("Board1").click();

    // Load the JSON fixture directly in the it block
    cy.fixture('movingCards.json').then((movingCardsData) => {
      // Iterate over each card in the JSON fixture
      movingCardsData.forEach((card) => {
        // Use the custom command to move each card
        cy.moveCard(card.fromList, card.cardName, card.toList);
        // Assert the card is no longer in the fromList
        cy.get('li')
          .find('h2')
          .contains(card.fromList)
          .closest('li')
          .find('div')
          .find('div')
          .should('not.contain', card.cardName); // Check card is not in the fromList

        // Assert the card is present in the toList
        cy.get('li')
          .find('h2')
          .contains(card.toList)
          .closest('li')
          .find('div')
          .find('a')
          .contains(card.cardName)
          .should('exist');
      });
    });
  });


  it(' Delete the Board1 ', () => {

    boardPage.getBoardByName("Board1").click();
    boardPage.getThereDotsNearShareButton().click();
    boardPage.getCloseBoard().click();
    boardPage.getCloseButton().click();
    boardPage.getThisBoardIsClosed().should('be.visible');
    boardPage.getPermanentlyDeleteBoardButton().click();
    boardPage.getDeleteButton().click();
    boardPage.getBoardDeletedPopup().should('be.visible')
  });



});

