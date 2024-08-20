Feature: Trello board automation

  Scenario: Create board, add lists, add cards, move cards, close and delete board
    Given I am logged in to Trello
    When I create a board named "VaultN"
    And I add lists "Backlog", "Todo", "Doing", "Testing", "Done" to the board
    And I create cards in "Todo" and "Backlog"
    Then I move the cards to their appropriate lists
    And I close and delete the board