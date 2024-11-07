package game;

import cards.HeroCard;
import cards.MinionCard;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.Coordinates;
import fileio.StartGameInput;
import player.Player;
import table.GameTable;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Rounds {
    private int manaPerRound;
    private int currentTurn;
    private ArrayList<Player> turns;
    private int gameEnded;

    public Rounds() {
        turns = new ArrayList<>(2);
        manaPerRound = 0;
        currentTurn = 0;
        gameEnded = 0;
    }

    public void start(ArrayNode output, StartGameInput startGame, ArrayList<ActionsInput> actions, GameTable table, Player playerOne, Player playerTwo, AtomicInteger gamesPlayerOneWon, AtomicInteger gamesPlayerTwoWon, int gamesPlayed ) {
        //Initialize order of players in turns
        if (startGame.getStartingPlayer() == 1) {
            turns.add(playerOne);
            turns.add(playerTwo);
        } else {
            turns.add(playerTwo);
            turns.add(playerOne);
        }

        //Start playing rounds
        while (playerOne.getPlayerHero().getHealth() > 0 && playerTwo.getPlayerHero().getHealth() > 0) {
            //Increment mana
            if (manaPerRound < 10) {
                manaPerRound++;
            }
            playerOne.addMana(manaPerRound);
            playerTwo.addMana(manaPerRound);

            //Get first card from deck
            playerOne.addCardToHand();
            playerTwo.addCardToHand();

            //Parses the actions array and prints each expected output
            int n = actions.size();
            for (int i = 0; i < n; ++i) {
                int roundIsFinished;
                ActionsInput action = actions.remove(0);
                roundIsFinished = actOnCommand(output, startGame, action, table, playerOne, playerTwo, gamesPlayerOneWon, gamesPlayerTwoWon, gamesPlayed);
                if (roundIsFinished == 1) {
                    break;
                }
            }
            if (actions.isEmpty()) {
                break;
            }
        }
    }

    public int actOnCommand(ArrayNode output, StartGameInput startGame, ActionsInput action, GameTable table, Player playerOne, Player playerTwo, AtomicInteger gamesPlayerOneWon, AtomicInteger gamesPlayerTwoWon, int gamesPlayed) {
        //Player whose turn is now
        Player player = turns.get(currentTurn % 2);

        ArrayList<MinionCard> deckPlayerOne = playerOne.getPlayerDeck();
        ArrayList<MinionCard> deckPlayerTwo = playerTwo.getPlayerDeck();

        ObjectMapper mapper = new ObjectMapper();
        int roundIsFinished = 0;
        switch (action.getCommand()) {
            case "getPlayerDeck":
                getPlayerDeck(output, action, deckPlayerOne, deckPlayerTwo);
                break;

            case "getPlayerHero":
                getPlayerHero(output, action, playerOne, playerTwo);
                break;

            case "getPlayerTurn":
                getPlayerTurn(output, action, startGame);
                break;

            case "endPlayerTurn":
                roundIsFinished = endPlayerTurn(table, player, playerOne, playerTwo);
                break;

            case "placeCard":
                placeCard(output, player, action, table);
                break;

            case "getCardsInHand":
                getCardsInHand(output, action, playerOne, playerTwo);
                break;

            case "getCardsOnTable":
                getCardsOnTable(output, action, table);
                break;

            case "getCardAtPosition":
                getCardAtPosition(output, action, table);
                break;

            case "getPlayerMana":
                getPlayerMana(output, action, playerOne, playerTwo);
                break;

            case "cardUsesAttack":
                cardUsesAttack(output, action, player, table);
                break;

            case "cardUsesAbility":
                cardUsesAbility(output, action, player, table);
                break;

            case "useAttackHero":
                useAttackHero(output, action, player, playerOne, playerTwo, table, gamesPlayerOneWon, gamesPlayerTwoWon);
                break;

            case "useHeroAbility":
                useHeroAbility(output, action, player, table);
                break;

            case "getFrozenCardsOnTable":
                getFrozenCardsOnTable(output, action, table);
                break;

            case "getPlayerOneWins":
                getPlayerOneWins(output, action, gamesPlayerOneWon);
                break;

            case "getPlayerTwoWins":
                getPlayerTwoWins(output, action, gamesPlayerTwoWon);
                break;

            case "getTotalGamesPlayed":
                getTotalGamesPlayed(output, action, gamesPlayed);
                break;

            default:
                System.out.println("Unknown command: " + action.getCommand());
                break;
        }
        if (roundIsFinished == 1) {
            return 1;
        }
        return 0;
    }

    public void getPlayerDeck(ArrayNode output, ActionsInput action, ArrayList<MinionCard> deckPlayerOne, ArrayList<MinionCard> deckPlayerTwo) {
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

    public void getPlayerHero(ArrayNode output, ActionsInput action, Player playerOne, Player playerTwo) {
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

    public void getPlayerTurn(ArrayNode output, ActionsInput action, StartGameInput startGame) {
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

    public int endPlayerTurn(GameTable table, Player player, Player playerOne, Player playerTwo) {
        currentTurn++;
        playerOne.getPlayerHero().setAttacked(0);
        playerTwo.getPlayerHero().setAttacked(0);
        for (int j = 0; j < 4; ++j) {
            for (int k = 0; k < 5; ++k) {
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

    public void placeCard(ArrayNode output, Player player, ActionsInput action, GameTable table) {
        ObjectMapper mapper = new ObjectMapper();
        int index = player.getPlayerIdx() - 1;
        MinionCard card = player.checkCardInHand(action.getHandIdx());
        if (card != null) {
            if (card.getMana() > player.getManaLeftToUse()) {
                ObjectNode placeCardNode = mapper.createObjectNode();
                placeCardNode.put("command", action.getCommand());
                placeCardNode.put("handIdx", action.getHandIdx());
                placeCardNode.put("error", "Not enough mana to place card on table.");
                output.add(placeCardNode);
            } else if (!table.isSpaceOnRow(card.determineRow(player.getPlayerIdx()))) {
                ObjectNode placeCardNode = mapper.createObjectNode();
                placeCardNode.put("command", action.getCommand());
                placeCardNode.put("handIdx", action.getHandIdx());
                placeCardNode.put("error", "Cannot place card on table since row is full.");
                output.add(placeCardNode);
            } else {
                table.placeCard(index, card.determineRow(player.getPlayerIdx()), card);
                player.getPlayerHand().remove(action.getHandIdx());
                player.useMana(card.getMana());
            }
        }
    }

    public void getCardsInHand(ArrayNode output, ActionsInput action, Player playerOne, Player playerTwo) {
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

    public void getCardsOnTable(ArrayNode output, ActionsInput action, GameTable table) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode getCardsOnTableNode = mapper.createObjectNode();
        getCardsOnTableNode.put("command", action.getCommand());
        ArrayNode tableArray = mapper.createArrayNode();
        for (int j = 0; j < 4; ++j) {
            ArrayNode rowArray = mapper.createArrayNode();
            for (int k = 0; k < 5; ++k) {
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

    public void getCardAtPosition(ArrayNode output, ActionsInput action, GameTable table) {
        int xCard = action.getX();
        int yCard = action.getY();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode errorNode = mapper.createObjectNode();
        errorNode.put("command", action.getCommand());
        errorNode.put("x", xCard);
        errorNode.put("y", yCard);

        if (table.getCard(xCard, yCard) == null) {
            errorNode.put("output", "No card available at that position.");
            output.add(errorNode);
        } else {
            ObjectNode cardNode = mapper.convertValue(table.getCard(xCard, yCard), ObjectNode.class);
            errorNode.set("output", cardNode);
            output.add(errorNode);
        }
    }

    public void getPlayerMana(ArrayNode output, ActionsInput action, Player playerOne, Player playerTwo) {
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

    public void cardUsesAttack(ArrayNode output, ActionsInput action, Player player, GameTable table) {
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

        if (attackerCard != null && attackedCard != null) {
            if ((xAttacker < 2 && xAttacked < 2) || (xAttacker > 1 && xAttacked > 1)) {
                ObjectNode errorCardNode = mapper.createObjectNode();
                errorCardNode.put("command", action.getCommand());
                ObjectNode attackerCardNode = mapper.convertValue(attackerCoord, ObjectNode.class);
                errorCardNode.set("cardAttacker", attackerCardNode);
                ObjectNode attackedCardNode = mapper.convertValue(attackedCoord, ObjectNode.class);
                errorCardNode.set("cardAttacked", attackedCardNode);
                errorCardNode.put("error", "Attacked card does not belong to the enemy.");
                output.add(errorCardNode);
                return;
            } else if (attackerCard.getAttacked() == 1) {
                ObjectNode errorCardNode = mapper.createObjectNode();
                errorCardNode.put("command", action.getCommand());
                ObjectNode attackerCardNode = mapper.convertValue(attackerCoord, ObjectNode.class);
                errorCardNode.set("cardAttacker", attackerCardNode);
                ObjectNode attackedCardNode = mapper.convertValue(attackedCoord, ObjectNode.class);
                errorCardNode.set("cardAttacked", attackedCardNode);
                errorCardNode.put("error", "Attacker card has already attacked this turn.");
                output.add(errorCardNode);
                return;
            } else if (attackerCard.getIsFrozen() == 1) {
                ObjectNode errorCardNode = mapper.createObjectNode();
                errorCardNode.put("command", action.getCommand());
                ObjectNode attackerCardNode = mapper.convertValue(attackerCoord, ObjectNode.class);
                errorCardNode.set("cardAttacker", attackerCardNode);
                ObjectNode attackedCardNode = mapper.convertValue(attackedCoord, ObjectNode.class);
                errorCardNode.set("cardAttacked", attackedCardNode);
                errorCardNode.put("error", "Attacker card is frozen.");
                output.add(errorCardNode);
                return;
            } else if (!table.getTankCoordinates(index).isEmpty()) {
                if (table.checkTanks(xAttacked, yAttacked, index) == -1) {
                    ObjectNode errorCardNode = mapper.createObjectNode();
                    errorCardNode.put("command", action.getCommand());
                    ObjectNode attackerCardNode = mapper.convertValue(attackerCoord, ObjectNode.class);
                    errorCardNode.set("cardAttacker", attackerCardNode);
                    ObjectNode attackedCardNode = mapper.convertValue(attackedCoord, ObjectNode.class);
                    errorCardNode.set("cardAttacked", attackedCardNode);
                    errorCardNode.put("error", "Attacked card is not of type 'Tank'.");
                    output.add(errorCardNode);
                } else {
                    int err = attackerCard.useAttackOnCard(attackedCard);
                    if (err == 1) {
                        table.removeTank(xAttacked, yAttacked, index);
                        table.removeCard(xAttacked, yAttacked);
                    }
                }
            } else {
                int err = attackerCard.useAttackOnCard(attackedCard);
                if (err == 1) {
                    table.removeCard(xAttacked, yAttacked);
                }
            }
        }
    }

    public void cardUsesAbility(ArrayNode output, ActionsInput action, Player player, GameTable table) {
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

        if (attackerCard != null && attackedCard != null) {
            if (attackerCard.getIsFrozen() == 1) {
                ObjectNode errorCardNode = mapper.createObjectNode();
                errorCardNode.put("command", action.getCommand());
                ObjectNode attackerCardNode = mapper.convertValue(attackerCoord, ObjectNode.class);
                errorCardNode.set("cardAttacker", attackerCardNode);
                ObjectNode attackedCardNode = mapper.convertValue(attackedCoord, ObjectNode.class);
                errorCardNode.set("cardAttacked", attackedCardNode);
                errorCardNode.put("error", "Attacker card is frozen.");
                output.add(errorCardNode);
                return;
            } else if (attackerCard.getAttacked() == 1) {
                ObjectNode errorCardNode = mapper.createObjectNode();
                errorCardNode.put("command", action.getCommand());
                ObjectNode attackerCardNode = mapper.convertValue(attackerCoord, ObjectNode.class);
                errorCardNode.set("cardAttacker", attackerCardNode);
                ObjectNode attackedCardNode = mapper.convertValue(attackedCoord, ObjectNode.class);
                errorCardNode.set("cardAttacked", attackedCardNode);
                errorCardNode.put("error", "Attacker card has already attacked this turn.");
                output.add(errorCardNode);
                return;
            } else  if (attackerCard.getName().equals("Disciple")) {
                if ((xAttacker < 2 && xAttacked > 1) || (xAttacker > 1 && xAttacked < 2)) {
                    ObjectNode errorCardNode = mapper.createObjectNode();
                    errorCardNode.put("command", action.getCommand());
                    ObjectNode attackerCardNode = mapper.convertValue(attackerCoord, ObjectNode.class);
                    errorCardNode.set("cardAttacker", attackerCardNode);
                    ObjectNode attackedCardNode = mapper.convertValue(attackedCoord, ObjectNode.class);
                    errorCardNode.set("cardAttacked", attackedCardNode);
                    errorCardNode.put("error", "Attacked card does not belong to the current player.");
                    output.add(errorCardNode);
                    return;
                } else {
                    int err = attackerCard.useAbility(attackedCard);
                    if (err == 1) {
                        table.removeCard(xAttacked, yAttacked);
                    }
                }
            } else if (attackerCard.getName().equals("The Ripper") || attackerCard.getName().equals("Miraj") || attackerCard.getName().equals("The Cursed One")) {
                if ((xAttacker < 2 && xAttacked < 2) || (xAttacker > 1 && xAttacked > 1)) {
                    ObjectNode errorCardNode = mapper.createObjectNode();
                    errorCardNode.put("command", action.getCommand());
                    ObjectNode attackerCardNode = mapper.convertValue(attackerCoord, ObjectNode.class);
                    errorCardNode.set("cardAttacker", attackerCardNode);
                    ObjectNode attackedCardNode = mapper.convertValue(attackedCoord, ObjectNode.class);
                    errorCardNode.set("cardAttacked", attackedCardNode);
                    errorCardNode.put("error", "Attacked card does not belong to the enemy.");
                    output.add(errorCardNode);
                    return;
                } else if (!table.getTankCoordinates(index).isEmpty()) {
                    if (table.checkTanks(xAttacked, yAttacked,index) == -1) {
                        ObjectNode errorCardNode = mapper.createObjectNode();
                        errorCardNode.put("command", action.getCommand());
                        ObjectNode attackerCardNode = mapper.convertValue(attackerCoord, ObjectNode.class);
                        errorCardNode.set("cardAttacker", attackerCardNode);
                        ObjectNode attackedCardNode = mapper.convertValue(attackedCoord, ObjectNode.class);
                        errorCardNode.set("cardAttacked", attackedCardNode);
                        errorCardNode.put("error", "Attacked card is not of type 'Tank'.");
                        output.add(errorCardNode);
                        return;
                    } else {
                        int err = attackerCard.useAbility(attackedCard);
                        if (err == 1) {
                            table.removeCard(xAttacked, yAttacked);
                            table.removeTank(xAttacked, yAttacked, index);
                        }
                    }
                } else {
                    int err = attackerCard.useAbility(attackedCard);
                    if (err == 1) {
                        table.removeCard(xAttacked, yAttacked);
                        table.removeTank(xAttacked, yAttacked, index);
                    }
                }
            }
        }
    }

    public void useAttackHero(ArrayNode output, ActionsInput action, Player player, Player playerOne, Player playerTwo, GameTable table, AtomicInteger gamesPlayerOneWon, AtomicInteger gamesPlayerTwoWon) {
        ObjectMapper mapper = new ObjectMapper();
        int index = 2 - player.getPlayerIdx();
        int xAttacker = action.getCardAttacker().getX();
        int yAttacker = action.getCardAttacker().getY();
        Coordinates attackerCoord = action.getCardAttacker();
        MinionCard attackerCard = table.getCard(xAttacker, yAttacker);

        if (attackerCard != null) {
            if (attackerCard.getIsFrozen() == 1) {
                ObjectNode errorCardNode = mapper.createObjectNode();
                errorCardNode.put("command", action.getCommand());
                ObjectNode attackerCardNode = mapper.convertValue(attackerCoord, ObjectNode.class);
                errorCardNode.set("cardAttacker", attackerCardNode);
                errorCardNode.put("error", "Attacker card is frozen.");
                output.add(errorCardNode);
                return;
            } else if (attackerCard.getAttacked() == 1) {
                ObjectNode errorCardNode = mapper.createObjectNode();
                errorCardNode.put("command", action.getCommand());
                ObjectNode attackerCardNode = mapper.convertValue(attackerCoord, ObjectNode.class);
                errorCardNode.set("cardAttacker", attackerCardNode);
                errorCardNode.put("error", "Attacker card has already attacked this turn.");
                output.add(errorCardNode);
                return;
            } else if (!table.getTankCoordinates(index).isEmpty()) {
                ObjectNode errorCardNode = mapper.createObjectNode();
                errorCardNode.put("command", action.getCommand());
                ObjectNode attackerCardNode = mapper.convertValue(attackerCoord, ObjectNode.class);
                errorCardNode.set("cardAttacker", attackerCardNode);
                errorCardNode.put("error", "Attacked card is not of type 'Tank'.");
                output.add(errorCardNode);
                return;
            } else {
                int err;
                if (player.getPlayerIdx() == 1) {
                    err = attackerCard.useAttackOnHero(playerTwo.getPlayerHero());
                    if (err == 1) {
                        gameEnded = 1;
                        gamesPlayerOneWon.set(gamesPlayerOneWon.get() + 1);
                        ObjectNode errorCardNode = mapper.createObjectNode();
                        errorCardNode.put("gameEnded", "Player one killed the enemy hero.");
                        output.add(errorCardNode);
                    }
                } else {
                    err = attackerCard.useAttackOnHero(playerOne.getPlayerHero());
                    if (err == 1) {
                        gameEnded = 1;
                        gamesPlayerTwoWon.set(gamesPlayerTwoWon.get() + 1);
                        ObjectNode errorCardNode = mapper.createObjectNode();
                        errorCardNode.put("gameEnded", "Player two killed the enemy hero.");
                        output.add(errorCardNode);
                    }
                }
            }
        }
    }

    public void useHeroAbility(ArrayNode output, ActionsInput action, Player player, GameTable table) {
        ObjectMapper mapper = new ObjectMapper();
        int affectedRow = action.getAffectedRow();
        HeroCard hero = player.getPlayerHero();
        if (hero.getMana() > player.getManaLeftToUse()) {
            ObjectNode errorCardNode = mapper.createObjectNode();
            errorCardNode.put("command", action.getCommand());
            errorCardNode.put("affectedRow", affectedRow);
            errorCardNode.put("error", "Not enough mana to use hero's ability.");
            output.add(errorCardNode);
            return;
        } else if (hero.getAttacked() == 1) {
            ObjectNode errorCardNode = mapper.createObjectNode();
            errorCardNode.put("command", action.getCommand());
            errorCardNode.put("affectedRow", affectedRow);
            errorCardNode.put("error", "Hero has already attacked this turn.");
            output.add(errorCardNode);
            return;
        } else if (hero.getName().equals("Lord Royce") || hero.getName().equals("Empress Thorina")) {
            if ((player.getPlayerIdx() == 1 && (affectedRow == 0 || affectedRow == 1)) || (player.getPlayerIdx() == 2 && (affectedRow == 2 || affectedRow == 3))) {
                hero.useAbility(table, affectedRow);
                player.useMana(hero.getMana());
            } else {
                ObjectNode errorCardNode = mapper.createObjectNode();
                errorCardNode.put("command", action.getCommand());
                errorCardNode.put("affectedRow", affectedRow);
                errorCardNode.put("error", "Selected row does not belong to the enemy.");
                output.add(errorCardNode);
                return;
            }
        } else if (hero.getName().equals("General Kocioraw") || hero.getName().equals("King Mudface")) {
            if ((affectedRow == (player.getPlayerIdx() + player.getPlayerIdx()) % 4) || (affectedRow == (player.getPlayerIdx() + player.getPlayerIdx() + 1) % 4)) {
                hero.useAbility(table, affectedRow);
                player.useMana(hero.getMana());
            } else {
                ObjectNode errorCardNode = mapper.createObjectNode();
                errorCardNode.put("command", action.getCommand());
                errorCardNode.put("affectedRow", affectedRow);
                errorCardNode.put("error", "Selected row does not belong to the current player.");
                output.add(errorCardNode);
            }
        }
    }

    public void getFrozenCardsOnTable(ArrayNode output, ActionsInput action, GameTable table) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode errorCardNode = mapper.createObjectNode();
        errorCardNode.put("command", action.getCommand());
        ArrayNode frozenArray = mapper.createArrayNode();
        for (int j = 0; j < 4; ++j) {
            for (int k = 0; k < 5; ++k) {
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

    public void getPlayerOneWins(ArrayNode output, ActionsInput action, AtomicInteger gamesPlayerOneWon) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode playerOneWinsNode = mapper.createObjectNode();
        playerOneWinsNode.put("command", action.getCommand());
        playerOneWinsNode.put("output", gamesPlayerOneWon.get());
        output.add(playerOneWinsNode);
    }

    public void getPlayerTwoWins(ArrayNode output, ActionsInput action, AtomicInteger gamesPlayerTwoWon) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode playerTwoWinsNode = mapper.createObjectNode();
        playerTwoWinsNode.put("command", action.getCommand());
        playerTwoWinsNode.put("output", gamesPlayerTwoWon.get());
        output.add(playerTwoWinsNode);
    }

    public void getTotalGamesPlayed(ArrayNode output, ActionsInput action, int gamesPlayed) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode totalGamesPlayedNode = mapper.createObjectNode();
        totalGamesPlayedNode.put("command", action.getCommand());
        totalGamesPlayedNode.put("output", gamesPlayed);
        output.add(totalGamesPlayedNode);
    }

}
