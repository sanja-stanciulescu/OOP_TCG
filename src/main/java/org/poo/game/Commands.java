package org.poo.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.cards.HeroCard;
import org.poo.cards.MinionCard;
import org.poo.fileio.ActionsInput;
import org.poo.fileio.Coordinates;
import org.poo.fileio.StartGameInput;
import org.poo.player.Player;
import org.poo.table.GameTable;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Commands {
    private int currentTurn;

    public Commands(final int currentTurn) {
        this.currentTurn = currentTurn;
    }

    /**
     * Retrieves the current turn value.
     *
     * @return the current turn value;
     */
    public int getCurrentTurn() {
        return currentTurn;
    }

    /**
     * Retrieves the deck of the specified player and adds it to the output.
     * <p>
     * This method checks the player index from the given action and retrieves
     * the corresponding deck(either player one's or player two's deck). It then
     * converts each {@link MinionCard} in the deck to a JSON object and adds it
     * to the output for the specified player.
     *
     * @param output an {@link ArrayNode} where the resulting deck information will
     * be added as output
     * @param action an {@link ActionsInput} object containing the action command and
     * the player index
     * @param deckPlayerOne an {@link ArrayList} of {@link MinionCard} representing
     * player one's deck
     * @param deckPlayerTwo an {@link ArrayList} of {@link MinionCard} representing
     * player two's deck
     */
    public void getPlayerDeck(
            final ArrayNode output,
            final ActionsInput action,
            final ArrayList<MinionCard> deckPlayerOne,
            final ArrayList<MinionCard> deckPlayerTwo
    ) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode getPlayerDeckNode = mapper.createObjectNode();
        getPlayerDeckNode.put("command", action.getCommand());
        getPlayerDeckNode.put("playerIdx", action.getPlayerIdx());

        ArrayList<MinionCard> deck;
        if (action.getPlayerIdx() == 1) {
            deck = deckPlayerOne;
        } else {
            deck = deckPlayerTwo;
        }

        ArrayNode deckArray = mapper.createArrayNode();
        for (MinionCard card : deck) {
            ObjectNode cardNode = mapper.convertValue(card, ObjectNode.class);
            deckArray.add(cardNode);
        }
        getPlayerDeckNode.set("output", deckArray);
        output.add(getPlayerDeckNode);
    }

    /**
     * Retrieves the hero card of the specified player and adds it to the output.
     * <p>
     * This method checks the player index from the given action and retrieves
     * the corresponding hero card (either player one's or player two's hero).
     * It then converts the hero card to a JSON object and adds it to the output
     * for the specified player.
     *
     * @param output an {@link ArrayNode} where the resulting hero card information
     * will be added as output
     * @param action an {@link ActionsInput} object containing the action command
     * and the player index
     * @param playerOne a {@link Player} object representing player one's information,
     * including their hero card
     * @param playerTwo a {@link Player} object representing player two's information,
     * including their hero card
     */
    public void getPlayerHero(
            final ArrayNode output,
            final ActionsInput action,
            final Player playerOne,
            final Player playerTwo
    ) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode getPlayerHeroNode = mapper.createObjectNode();
        getPlayerHeroNode.put("command", action.getCommand());
        getPlayerHeroNode.put("playerIdx", action.getPlayerIdx());

        ObjectNode heroNode;
        if (action.getPlayerIdx() == 1) {
            heroNode = mapper.convertValue(playerOne.getPlayerHero(), ObjectNode.class);
        } else {
            heroNode = mapper.convertValue(playerTwo.getPlayerHero(), ObjectNode.class);
        }
        getPlayerHeroNode.set("output", heroNode);
        output.add(getPlayerHeroNode);
    }

    /**
     * Retrieves the current player's turn and adds it to the output.
     * <p>
     * This method calculates which player's turn it is based on the starting player
     * and the current turn in the game. It then adds the information to the output
     * in the format specified by the action.
     *
     * @param output an {@link ArrayNode} where the resulting player turn information
     * will be added as output
     * @param action an {@link ActionsInput} object containing the action command and
     * the player index
     * @param startGame a {@link StartGameInput} object containing the starting player information
     */
    public void getPlayerTurn(
            final ArrayNode output,
            final ActionsInput action,
            final StartGameInput startGame
    ) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode getPlayerTurnNode = mapper.createObjectNode();
        getPlayerTurnNode.put("command", action.getCommand());

        int currentPlayer;
        if (startGame.getStartingPlayer() == 1) {
            currentPlayer = 1 + currentTurn % 2;
        } else if (currentTurn % 2 == 0) {
            currentPlayer = 2;
        } else {
            currentPlayer = 1;
        }
        getPlayerTurnNode.put("output", currentPlayer);
        output.add(getPlayerTurnNode);
    }

    /**
     * Ends the current player's turn and prepares for the next turn.
     * <p>
     * This method updates the game state at the end of a player's turn.
     * It resets the 'attacked' status for the player's hero and all cards on the table.
     * Additionally, it handles the freezing effect based on the player's index.
     * The method returns 1 if the round is finished (i.e., after both players have
     * completed their turns), otherwise it returns 0 to indicate that the game is continuing.
     *
     * @param table the {@link GameTable} object representing the game board with cards placed
     * @param player the {@link Player} whose turn is ending
     * @param playerOne the {@link Player} representing player one
     * @param playerTwo the {@link Player} representing player two
     * @return 1 if the round is finished (both players have completed their turns), otherwise 0
     */
    public int endPlayerTurn(
            final GameTable table,
            final Player player,
            final Player playerOne,
            final Player playerTwo
    ) {
        currentTurn++;
        playerOne.getPlayerHero().setAttacked(0);
        playerTwo.getPlayerHero().setAttacked(0);
        for (int j = 0; j < GameTable.ROWS_NO; ++j) {
            for (int k = 0; k < GameTable.COLS_NO; ++k) {
                if (table.getCard(j, k) != null) {
                    table.getCard(j, k).setAttacked(0);
                    if (player.getPlayerIdx() == 1 && (j == 2 || j == 3)) {
                        table.getCard(j, k).setIsFrozen(0);
                    } else if (player.getPlayerIdx() == 2 && (j == 0 || j == 1)) {
                        table.getCard(j, k).setIsFrozen(0);
                    }
                }
            }
        }
        if (currentTurn % 2 == 0 && currentTurn != 0) {
            return 1;
        }
        return 0;
    }

    /**
     * Places a card from the player's hand onto the game table.
     * <p>
     * This method handles the logic of placing a card from the player's hand to the game table.
     * It checks if the player has enough mana to place the card and if there is space
     * in the chosen row on the table. If either condition is not met, it adds an error message
     * to the output.
     * If the card is successfully placed, it removes the card from the player's hand and deducts
     * the required mana.
     *
     * @param output the {@link ArrayNode} to which the output (including errors) will be added
     * @param player the {@link Player} placing the card on the table
     * @param action the {@link ActionsInput} containing the details of the action
     * @param table the {@link GameTable} where the cards are placed
     */
    public void placeCard(
            final ArrayNode output,
            final Player player,
            final ActionsInput action,
            final GameTable table
    ) {
        ObjectMapper mapper = new ObjectMapper();
        int index = player.getPlayerIdx() - 1;
        try {
            MinionCard card = player.checkCardInHand(action.getHandIdx());

            if (card == null) {
                throw new IllegalArgumentException("Card does not exist in player's hand.");
            }

            if (card.getMana() > player.getManaLeftToUse()) {
                throw new InsufficientManaException("Not enough mana to place card on table.");
            }
            if (!table.isSpaceOnRow(card.determineRow(player.getPlayerIdx()))) {
                throw new RowFullException("Cannot place card on table since row is full.");
            }

            table.placeCard(index, card.determineRow(player.getPlayerIdx()), card);
            player.getPlayerHand().remove(action.getHandIdx());
            player.useMana(card.getMana());

        } catch (InsufficientManaException | RowFullException e) {
            ObjectNode errorNode = mapper.createObjectNode();
            errorNode.put("command", action.getCommand());
            errorNode.put("handIdx", action.getHandIdx());
            errorNode.put("error", e.getMessage());
            output.add(errorNode);
        }
    }

    /**
     * Retrieves the cards in hand of a specified player and adds them to the output.
     * <p>
     * This method checks which player’s hand to retrieve (based on the player index)
     * and converts each card in the hand to a JSON object. The resulting list of cards
     * is added to the output, which will be used for further processing or response generation.
     *
     * @param output the {@link ArrayNode} to which the list of cards in hand will be added
     * @param action the {@link ActionsInput} containing the player's index to identify
     * the correct player
     * @param playerOne the {@link Player} object representing the first player
     * @param playerTwo the {@link Player} object representing the second player
     */
    public void getCardsInHand(
            final ArrayNode output,
            final ActionsInput action,
            final Player playerOne,
            final Player playerTwo
    ) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode getCardsInHandNode = mapper.createObjectNode();
        getCardsInHandNode.put("command", action.getCommand());
        getCardsInHandNode.put("playerIdx", action.getPlayerIdx());
        ArrayList<MinionCard> hand;
        if (action.getPlayerIdx() == 1) {
            hand = playerOne.getPlayerHand();
        } else {
            hand = playerTwo.getPlayerHand();
        }
        ArrayNode handArray = mapper.createArrayNode();
        for (MinionCard minion : hand) {
            ObjectNode cardNode = mapper.convertValue(minion, ObjectNode.class);
            handArray.add(cardNode);
        }
        getCardsInHandNode.set("output", handArray);
        output.add(getCardsInHandNode);
    }

    /**
     * Retrieves all the cards currently placed on the table and adds them to the output.
     * <p>
     * This method iterates through all the rows and columns of the game table, checking if
     * there are cards present. If a card exists in a given cell, it is converted to a JSON
     * object and added to the respective row.
     * Once all cards are processed, the resulting table data is added to the output.
     *
     * @param output the {@link ArrayNode} to which the cards on the table will be added
     * @param action the {@link ActionsInput} containing the command information
     * @param table the {@link GameTable} object containing the current state of the game table
     */
    public void getCardsOnTable(
            final ArrayNode output,
            final ActionsInput action,
            final GameTable table
    ) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode getCardsOnTableNode = mapper.createObjectNode();
        getCardsOnTableNode.put("command", action.getCommand());
        ArrayNode tableArray = mapper.createArrayNode();
        for (int j = 0; j < GameTable.ROWS_NO; ++j) {
            ArrayNode rowArray = mapper.createArrayNode();
            for (int k = 0; k < GameTable.COLS_NO; ++k) {
                MinionCard cardFromTable = table.getCard(j, k);
                if (cardFromTable != null) {
                    ObjectNode cardNode = mapper.convertValue(cardFromTable, ObjectNode.class);
                    rowArray.add(cardNode);
                }
            }
            tableArray.add(rowArray);
        }
        getCardsOnTableNode.set("output", tableArray);
        output.add(getCardsOnTableNode);
    }

    /**
     * Retrieves a card at a specific position on the game table and adds it to the output.
     * <p>
     * This method checks if a card is present at the given (x, y) position on the table.
     * If a card exists, it is converted to a JSON object and included in the output.
     * If no card exists at the specified position,an error message is added to the output.
     *
     * @param output the {@link ArrayNode} to which the card at the given position will be added
     * @param action the {@link ActionsInput} containing the command and position data (x and y)
     * @param table the {@link GameTable} object containing the current state of the game table
     */
    public void getCardAtPosition(
            final ArrayNode output,
            final ActionsInput action,
            final GameTable table
    ) {
        int xCard = action.getX();
        int yCard = action.getY();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode errorNode = mapper.createObjectNode();
        errorNode.put("command", action.getCommand());
        errorNode.put("x", xCard);
        errorNode.put("y", yCard);

        try {
            MinionCard card = table.getCard(xCard, yCard);
            if (card == null) {
                throw new CardNotFoundException("No card available at that position.");
            }

            ObjectNode cardNode = mapper.convertValue(card, ObjectNode.class);
            errorNode.set("output", cardNode);
            output.add(errorNode);

        } catch (CardNotFoundException e) {
            errorNode.put("output", e.getMessage());
            output.add(errorNode);
        }
    }

    /**
     * Retrieves the remaining mana of a specified player and adds it to the output.
     * <p>
     * This method checks which player is specified by the action and retrieves the amount
     * of mana left for that player. The mana value is then added to the output as a JSON object.
     *
     * @param output the {@link ArrayNode} to which the player's remaining mana will be added
     * @param action the {@link ActionsInput} containing the command and player index
     * @param playerOne the {@link Player} object representing player one
     * @param playerTwo the {@link Player} object representing player two
     */
    public void getPlayerMana(
            final ArrayNode output,
            final ActionsInput action,
            final Player playerOne,
            final Player playerTwo
    ) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode getPlayerManaNode = mapper.createObjectNode();
        getPlayerManaNode.put("command", action.getCommand());
        getPlayerManaNode.put("playerIdx", action.getPlayerIdx());
        if (action.getPlayerIdx() == 1) {
            getPlayerManaNode.put("output", playerOne.getManaLeftToUse());
        } else {
            getPlayerManaNode.put("output", playerTwo.getManaLeftToUse());
        }
        output.add(getPlayerManaNode);
    }

    /**
     * Processes the attack action where one card attacks another on the game table.
     * <p>
     * This method handles the logic for an attacker card using its attack on a target card.
     * It checks for various conditions such as whether the cards are on the correct sides
     * of the board, whether the attacker has already attacked, if the attacker is frozen,
     * and whether the target is a valid 'Tank' card.
     * If any conditions are not met, an error is added to the output.
     * If the conditions are met, the attack is executed and the affected card may be removed
     * from the table.
     *
     * @param output the {@link ArrayNode} to which the results or errors will be added
     * @param action the {@link ActionsInput} containing the details of the attack action
     * @param player the {@link Player} initiating the attack
     * @param table the {@link GameTable} representing the current game board state
     */
    public void cardUsesAttack(
            final ArrayNode output,
            final ActionsInput action,
            final Player player,
            final GameTable table
    ) {
        ObjectMapper mapper = new ObjectMapper();
        int index = 2 - player.getPlayerIdx();

        int xAttacker = action.getCardAttacker().getX();
        int yAttacker = action.getCardAttacker().getY();
        Coordinates attackerCoord = action.getCardAttacker();
        MinionCard attackerCard = table.getCard(xAttacker, yAttacker);

        int xAttacked = action.getCardAttacked().getX();
        int yAttacked = action.getCardAttacked().getY();
        Coordinates attackedCoord = action.getCardAttacked();
        MinionCard attackedCard = table.getCard(xAttacked, yAttacked);

        try {
            if (attackerCard == null || attackedCard == null) {
                throw new IllegalArgumentException("No attacker or attacked Card found.");
            }

            if ((xAttacker < 2 && xAttacked < 2) || (xAttacker > 1 && xAttacked > 1)) {
                throw new SameSideAttackException("Attacked card does not belong to the enemy.");
            }

            if (attackerCard.getAttacked() == 1) {
                throw new CardAlreadyAttackedException(
                        "Attacker card has already attacked this turn."
                );
            }

            if (attackerCard.getIsFrozen() == 1) {
                throw new CardFrozenException("Attacker card is frozen.");
            }

            if (!table.getTankCoordinates(index).isEmpty()
                    && table.checkTanks(xAttacked, yAttacked, index) == -1) {
                throw new InvalidTargetException("Attacked card is not of type 'Tank'.");
            }

            int err = attackerCard.useAttackOnCard(attackedCard);
            if (err == 1) {
                table.removeTank(xAttacked, yAttacked, index);
                table.removeCard(xAttacked, yAttacked);
            }

        } catch (SameSideAttackException | CardAlreadyAttackedException
                 | CardFrozenException | InvalidTargetException e) {
            ObjectNode errorCardNode = mapper.createObjectNode();
            errorCardNode.put("command", action.getCommand());
            ObjectNode attackerCardNode = mapper.convertValue(
                    attackerCoord,
                    ObjectNode.class
            );
            errorCardNode.set("cardAttacker", attackerCardNode);
            ObjectNode attackedCardNode = mapper.convertValue(
                    attackedCoord,
                    ObjectNode.class
            );
            errorCardNode.set("cardAttacked", attackedCardNode);
            errorCardNode.put("error", e.getMessage());
            output.add(errorCardNode);
        }
    }

    /**
     * Processes the ability usage action where a card uses its special ability on another card.
     * <p>
     * This method handles the logic for a card using its ability on a target card.
     * It checks for various conditions such as whether the attacker is frozen,
     * whether the attacker has already attacked, and whether the card names match
     * specific criteria.
     * If any of these conditions are not met, an error is added to the output.
     * If the conditions are met, the ability is executed and the affected card may be
     * removed from the table.
     * <p>
     * Special cases are handled for specific cards:
     * <ul>
     *     <li>For the "Disciple" card, the attack must be on a card belonging to the
     * current player.</li>
     *     <li>For the "The Ripper", "Miraj", and "The Cursed One" cards, the attacked card
     * must belong to the enemy and must be of type 'Tank'.</li>
     * </ul>
     *
     * @param output the {@link ArrayNode} to which the results or errors will be added
     * @param action the {@link ActionsInput} containing the details of the ability usage action
     * @param player the {@link Player} initiating the ability usage
     * @param table the {@link GameTable} representing the current game board state
     */
    public void cardUsesAbility(
            final ArrayNode output,
            final ActionsInput action,
            final Player player,
            final GameTable table
    ) {
        ObjectMapper mapper = new ObjectMapper();
        int index = 2 - player.getPlayerIdx();

        int xAttacker = action.getCardAttacker().getX();
        int yAttacker = action.getCardAttacker().getY();
        Coordinates attackerCoord = action.getCardAttacker();
        MinionCard attackerCard = table.getCard(xAttacker, yAttacker);

        int xAttacked = action.getCardAttacked().getX();
        int yAttacked = action.getCardAttacked().getY();
        Coordinates attackedCoord = action.getCardAttacked();
        MinionCard attackedCard = table.getCard(xAttacked, yAttacked);

        try {
            if (attackerCard == null || attackedCard == null) {
                throw new IllegalArgumentException("No attacker or attacked Card found.");
            }

            if (attackerCard.getIsFrozen() == 1) {
                throw new CardFrozenException("Attacker card is frozen.");
            }

            if (attackerCard.getAttacked() == 1) {
                throw new CardAlreadyAttackedException(
                        "Attacker card has already attacked this turn."
                );
            }

            if (attackerCard.getName().equals("Disciple")) {
                if ((xAttacker < 2 && xAttacked > 1) || (xAttacker > 1 && xAttacked < 2)) {
                    throw new InvalidTargetException(
                            "Attacked card does not belong to the current player."
                    );
                }
            } else if (attackerCard.getName().equals("The Ripper")
                    || attackerCard.getName().equals("Miraj")
                    || attackerCard.getName().equals("The Cursed One")) {
                if ((xAttacker < 2 && xAttacked < 2) || (xAttacker > 1 && xAttacked > 1)) {
                    throw new SameSideAttackException(
                            "Attacked card does not belong to the enemy."
                    );
                }

                if (!table.getTankCoordinates(index).isEmpty() &&
                        table.checkTanks(xAttacked, yAttacked, index) == -1) {
                    throw new InvalidTargetException("Attacked card is not of type 'Tank'.");
                }
            }

            int err = attackerCard.useAbility(attackedCard);
            if (err == 1) {
                table.removeTank(xAttacked, yAttacked, index);
                table.removeCard(xAttacked, yAttacked);
            }

        } catch (CardFrozenException | CardAlreadyAttackedException
                 | InvalidTargetException | SameSideAttackException e) {
            ObjectNode errorCardNode = mapper.createObjectNode();
            errorCardNode.put("command", action.getCommand());
            ObjectNode attackerCardNode = mapper.convertValue(attackerCoord, ObjectNode.class);
            errorCardNode.set("cardAttacker", attackerCardNode);
            ObjectNode attackedCardNode = mapper.convertValue(attackedCoord, ObjectNode.class);
            errorCardNode.set("cardAttacked", attackedCardNode);
            errorCardNode.put("error", e.getMessage());
            output.add(errorCardNode);
        }
    }

    /**
     * Handles the action where a player's minion card attacks the opposing player's hero.
     * The method checks several conditions before performing the attack:
     * <ul>
     *   <li>If the attacking card is frozen, the attack is prevented.</li>
     *   <li>If the attacking card has already attacked during the current turn,
     * the attack is prevented.</li>
     *   <li>If the player has any "Tank" cards on the table, the attack will fail
     * if the attacked card is not of type 'Tank'.</li>
     *   <li>If all conditions are met, the attack is carried out and the opposing
     * player's hero receives damage.</li>
     * </ul>
     * If the attack results in the hero's death, the game ends and the winner is recorded.
     *
     * @param output the {@link ArrayNode} to store the results or errors generated
     * during the action.
     * @param action the {@link ActionsInput} containing the action details, including
     * the attacker and the target hero.
     * @param player the {@link Player} who is performing the attack.
     * @param playerOne the first player in the game.
     * @param playerTwo the second player in the game.
     * @param table the {@link GameTable} representing the current state of the game.
     * @param gamesPlayerOneWon Atomic counter tracking the number of games won by player one.
     * @param gamesPlayerTwoWon Atomic counter tracking the number of games won by player two.
     */
    public void useAttackHero(
            final ArrayNode output,
            final ActionsInput action,
            final Player player,
            final Player playerOne,
            final Player playerTwo,
            final GameTable table,
            final AtomicInteger gamesPlayerOneWon,
            final AtomicInteger gamesPlayerTwoWon
    ) {
        ObjectMapper mapper = new ObjectMapper();
        int index = 2 - player.getPlayerIdx();

        int xAttacker = action.getCardAttacker().getX();
        int yAttacker = action.getCardAttacker().getY();
        Coordinates attackerCoord = action.getCardAttacker();
        MinionCard attackerCard = table.getCard(xAttacker, yAttacker);

        try {
            if (attackerCard == null) {
                throw new IllegalArgumentException("No attacker Card found.");
            }

            if (attackerCard.getIsFrozen() == 1) {
                throw new CardFrozenException("Attacker card is frozen.");
            }

            if (attackerCard.getAttacked() == 1) {
                throw new CardAlreadyAttackedException(
                        "Attacker card has already attacked this turn."
                );
            }

            if (!table.getTankCoordinates(index).isEmpty()) {
                throw new InvalidTargetException("Attacked card is not of type 'Tank'.");
            }

            int err;
            if (player.getPlayerIdx() == 1) {
                err = attackerCard.useAttackOnHero(playerTwo.getPlayerHero());
                if (err == 1) {
                    gamesPlayerOneWon.set(gamesPlayerOneWon.get() + 1);
                    ObjectNode errorCardNode = mapper.createObjectNode();
                    errorCardNode.put("gameEnded", "Player one killed the enemy hero.");
                    output.add(errorCardNode);
                }
            } else {
                err = attackerCard.useAttackOnHero(playerOne.getPlayerHero());
                if (err == 1) {
                    gamesPlayerTwoWon.set(gamesPlayerTwoWon.get() + 1);
                    ObjectNode errorCardNode = mapper.createObjectNode();
                    errorCardNode.put("gameEnded", "Player two killed the enemy hero.");
                    output.add(errorCardNode);
                }
            }
        } catch (CardFrozenException | CardAlreadyAttackedException | InvalidTargetException e) {
            ObjectNode errorCardNode = mapper.createObjectNode();
            errorCardNode.put("command", action.getCommand());
            ObjectNode attackerCardNode = mapper.convertValue(attackerCoord, ObjectNode.class);
            errorCardNode.set("cardAttacker", attackerCardNode);
            errorCardNode.put("error", e.getMessage());
            output.add(errorCardNode);
        }
    }

    /**
     * Executes the ability of a player's hero, provided the necessary conditions are met.
     * The method checks if the player has enough mana to use the hero's ability, if the hero
     * has already attacked, and whether the selected row for the ability is valid based on the
     * hero's name and the current player's position.
     * If any conditions are not met, an appropriate error message is added to the output.
     * If all conditions are met, the hero's ability is executed and the player's mana
     * is updated accordingly.
     *
     * @param output the {@link ArrayNode} to store the results or errors generated
     * during the action.
     * @param action the {@link ActionsInput} containing the action details, including
     * the affected row and command.
     * @param player the {@link Player} performing the action.
     * @param table the {@link GameTable} representing the current state of the game.
     */
    public void useHeroAbility(
            final ArrayNode output,
            final ActionsInput action,
            final Player player,
            final GameTable table
    ) {
        ObjectMapper mapper = new ObjectMapper();
        int affectedRow = action.getAffectedRow();
        HeroCard hero = player.getPlayerHero();

        try {
            if (hero.getMana() > player.getManaLeftToUse()) {
                throw new InsufficientManaException("Not enough mana to use hero's ability.");
            }

            if (hero.getAttacked() == 1) {
                throw new HeroAlreadyAttackedException("Hero has already attacked this turn.");
            }

            if (hero.getName().equals("Lord Royce")
                    || hero.getName().equals("Empress Thorina")) {
                if ((player.getPlayerIdx() == 1
                        && (affectedRow == 2 || affectedRow == 3))
                        || (player.getPlayerIdx() == 2
                        && (affectedRow == 0 || affectedRow == 1))) {
                    throw new InvalidRowSelectionException("Selected row does not belong to the enemy.");
                }
            } else if (hero.getName().equals("General Kocioraw")
                    || hero.getName().equals("King Mudface")) {
                if ((affectedRow != (player.getPlayerIdx() + player.getPlayerIdx()) % GameTable.ROWS_NO)
                        && (affectedRow != (player.getPlayerIdx() + player.getPlayerIdx() + 1) % 4)) {
                    throw new InvalidRowSelectionException(
                            "Selected row does not belong to the current player."
                    );
                }
            }

            hero.useAbility(table, affectedRow);
            player.useMana(hero.getMana());

        } catch (InsufficientManaException | HeroAlreadyAttackedException
                 | InvalidRowSelectionException e) {
            ObjectNode errorCardNode = mapper.createObjectNode();
            errorCardNode.put("command", action.getCommand());
            errorCardNode.put("affectedRow", affectedRow);
            errorCardNode.put("error", e.getMessage());
            output.add(errorCardNode);
        }
    }

    /**
     * Retrieves all frozen cards on the game table and adds them to the output.
     * The method iterates over the game table to check each card's frozen status.
     * If a card is frozen (i.e., its `getIsFrozen()` method returns 1), it is added
     * to the list of frozen cards.
     * The resulting list of frozen cards is included in the output, along with the
     * command and any necessary metadata.
     *
     * @param output the {@link ArrayNode} to store the results, including the frozen cards
     * found on the table.
     * @param action the {@link ActionsInput} containing the command that initiated this action.
     * @param table the {@link GameTable} representing the current state of the game,
     * used to access the cards on the table.
     */
    public void getFrozenCardsOnTable(
            final ArrayNode output,
            final ActionsInput action,
            final GameTable table
    ) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode errorCardNode = mapper.createObjectNode();
        errorCardNode.put("command", action.getCommand());
        ArrayNode frozenArray = mapper.createArrayNode();
        for (int j = 0; j < GameTable.ROWS_NO; ++j) {
            for (int k = 0; k < GameTable.COLS_NO; ++k) {
                MinionCard cardFromTable = table.getCard(j, k);
                if (cardFromTable != null) {
                    if (cardFromTable.getIsFrozen() == 1) {
                        ObjectNode cardNode = mapper.convertValue(cardFromTable, ObjectNode.class);
                        frozenArray.add(cardNode);
                    }
                }
            }
        }
        errorCardNode.set("output", frozenArray);
        output.add(errorCardNode);
    }

    /**
     * Retrieves the number of games won by Player One and adds it to the output.
     * The method creates a JSON node containing the command and the current number
     * of games Player One has won.
     * This data is then added to the output, allowing the system to report Player One's win count.
     *
     * @param output the {@link ArrayNode} to store the results, including the number of wins
     * for Player One.
     * @param action the {@link ActionsInput} containing the command that triggered this action.
     * @param gamesPlayerOneWon an `AtomicInteger` representing the number of games Player One
     * has won.
     */
    public void getPlayerOneWins(
            final ArrayNode output,
            final ActionsInput action,
            final AtomicInteger gamesPlayerOneWon
    ) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode playerOneWinsNode = mapper.createObjectNode();
        playerOneWinsNode.put("command", action.getCommand());
        playerOneWinsNode.put("output", gamesPlayerOneWon.get());
        output.add(playerOneWinsNode);
    }

    /**
     * Retrieves the number of games won by Player Two and adds it to the output.
     * The method creates a JSON node containing the command and the current number
     * of games Player Two has won.
     * This data is then added to the output, allowing the system to report Player Two's win count.
     *
     * @param output the {@link ArrayNode} to store the results, including the number of wins
     * for Player Two.
     * @param action the {@link ActionsInput} containing the command that triggered this action.
     * @param gamesPlayerTwoWon an `AtomicInteger` representing the number of games Player Two
     * has won.
     */
    public void getPlayerTwoWins(
            final ArrayNode output,
            final ActionsInput action,
            final AtomicInteger gamesPlayerTwoWon
    ) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode playerTwoWinsNode = mapper.createObjectNode();
        playerTwoWinsNode.put("command", action.getCommand());
        playerTwoWinsNode.put("output", gamesPlayerTwoWon.get());
        output.add(playerTwoWinsNode);
    }

    /**
     * Retrieves the total number of games played and adds it to the output.
     * This method creates a JSON node containing the command and the total number of games played,
     * and adds this data to the output for reporting purposes.
     *
     * @param output the {@link ArrayNode} to store the results, including the total number
     * of games played.
     * @param action the {@link ActionsInput} containing the command that triggered this action.
     * @param gamesPlayed an integer representing the total number of games played.
     */
    public void getTotalGamesPlayed(
            final ArrayNode output,
            final ActionsInput action,
            final int gamesPlayed) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode totalGamesPlayedNode = mapper.createObjectNode();
        totalGamesPlayedNode.put("command", action.getCommand());
        totalGamesPlayedNode.put("output", gamesPlayed);
        output.add(totalGamesPlayedNode);
    }
}
