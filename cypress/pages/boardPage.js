class BoardPage{


   getCreateButton(){
      return cy.get('[data-testid="AddIcon"]');
   }

   getCreateABoardButton(){
      return cy.contains('Create board');
   }


   getCreateButtonInPopup(){
    return cy.get("button[data-testid='create-board-submit-button']");
  }
   

   getBoardTitleInputBox(){
      return cy.get("input[type='text']");
   }

   getEnterListNameBox(){
      return cy.get("textarea[placeholder='Enter list name…']")
   }

   getBoardByName(boardName){
      return cy.contains(boardName);
   }

    getAddListButton(){
       return cy.contains('Add a list')
    }

    
     // Method to get the "Add a card" button for a specific list
     getAddCardButtonForList(listName) {
      return cy.contains('h2', listName) // Locate the h2 with the specific listName
          .closest('li') // Navigate up to the closest <li> ancestor
          .find('button') // Find button within this <li>
          .contains('Add a card'); // Ensure it contains the text 'Add a card'
  }

    getCardNameInputBox(){
      return cy.get(".qJv26NWQGVKzI9")
    }

    getAddCardButton(){
      return cy.contains("Add a card")
    }

    getThreeDotsNearTheShare(){
      return cy.get("button[aria-label='Show menu']")
    }


    getMoveButton(){
      return cy.get("a[title='Move']");
    }

   getListDropDown(){
      return cy.get('[data-testid="move-card-popover-select-list-destination"]');
    }

    getMoveButtonInMoveCardPopup(){
      return cy.get("button[data-testid$='move-card-popover-move-button']");
    }

    getCloseButtonInEditCardPopup(){
      return cy.get("span[data-testid$='CloseIcon']").first();
    }

    getThereDotsNearShareButton(){
      return cy.get("button[aria-label='Show menu']")
    }
    
    getThisBoardIsClosed(){
      return cy.get("div[class='xJP6EH9jYQiWkk'] p")
    }
    
    getCloseBoard(){
      return cy.contains("Close board")
    }

    getCloseButton(){
      return cy.get("input[value='Close']")
    }

    getPermanentlyDeleteBoardButton(){
      return cy.contains("Permanently delete board")
    }
    
    getDeleteButton(){
      return cy.get("button[data-testid*='close-board-delete-board-confirm-button']")
    }
    
    getBoardDeletedPopup(){
      return cy.get(".QMKgZFIlTLiEJN")
    }


   



    
   
}

export default BoardPage;