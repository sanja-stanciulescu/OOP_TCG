package main;

import cards.HeroCard;
import cards.MinionCard;
import checker.Checker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.node.ArrayNode;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.*;
import player.Player;
import table.GameTable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

import static java.util.Collections.shuffle;

/**
 * The entry point to this homework. It runs the checker that tests your implementation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Input inputData = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH + filePath1),
                Input.class);

        ArrayNode output = objectMapper.createArrayNode();

        /*
         * TODO Implement your function here
         *
         * How to add output to the output array?
         * There are multiple ways to do this, here is one example:
         */
        //System.out.println(inputData);

        ObjectMapper mapper = new ObjectMapper();
        //mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector());

        ArrayNode objectNode = mapper.createArrayNode();

        //Go through multiple games
        ArrayList<GameInput> games = inputData.getGames();
        for (GameInput game : games) {
            StartGameInput startGame = game.getStartGame();
            ArrayList<ActionsInput> actions = game.getActions();

            //Initialize the two players:
            //1. Pick chosen deck
            ArrayList<CardInput> deckOne = new ArrayList<>(
                    inputData.getPlayerOneDecks()
                            .getDecks()
                            .get(startGame.getPlayerOneDeckIdx())
            );

            ArrayList<CardInput> deckTwo = new ArrayList<>(
                    inputData.getPlayerTwoDecks()
                            .getDecks()
                            .get(startGame.getPlayerTwoDeckIdx())
            );

            //2. Give deck to the players
            ArrayList<MinionCard> deckPlayerOne = new ArrayList<MinionCard>();
            ArrayList<MinionCard> deckPlayerTwo = new ArrayList<MinionCard>();

            for (CardInput card : deckOne) {
                MinionCard minionCard = new MinionCard(card);
                deckPlayerOne.add(minionCard);
            }

            for (CardInput card : deckTwo) {
                MinionCard minionCard = new MinionCard(card);
                deckPlayerTwo.add(minionCard);
            }

            //3. Shuffle chosen deck
            Random rand1 = new Random(startGame.getShuffleSeed());
            Collections.shuffle(deckPlayerOne, rand1);

            Random rand2 = new Random(startGame.getShuffleSeed());
            Collections.shuffle(deckPlayerTwo, rand2);

            //4. Give hero card to the players
            CardInput heroInput = startGame.getPlayerOneHero();
            HeroCard playerOneHero = new HeroCard(heroInput);
            heroInput = startGame.getPlayerTwoHero();
            HeroCard playerTwoHero = new HeroCard(heroInput);

            Player playerOne = new Player(1, deckPlayerOne, playerOneHero);
            Player playerTwo = new Player(2, deckPlayerTwo, playerTwoHero);

            //Initializes the start of the rounds
            int manaPerRound = 0;
            int currentTurn = 0;
            GameTable table = new GameTable();

            //Initialize order of players in turns
            ArrayList<Player> turns = new ArrayList<Player>();
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
                if (manaPerRound <= 10) {
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
                    //Player whose turn is now
                    Player player = turns.get(currentTurn % 2);

                    int roundIsFinished = 0;
                    ActionsInput action = actions.remove(0);
                    switch (action.getCommand()) {
                        case "getPlayerDeck":
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
                            break;

                        case "getPlayerHero":
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
                            break;

                        case "getPlayerTurn":
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
                            break;

                        case "endPlayerTurn":
                            currentTurn++;
                            for (MinionCard card : deckPlayerOne) {
                                card.setAttacked(0);
                            }
                            for (MinionCard card : deckPlayerTwo) {
                                card.setAttacked(0);
                            }
                            if (currentTurn % 2 == 0 && currentTurn != 0) {
                                roundIsFinished = 1;
                            }
                            break;

                        case "placeCard":
                            MinionCard card = player.putCardOnTable(action.getHandIdx());
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
                                    table.placeCard(card.determineRow(player.getPlayerIdx()), card);
                                    player.getPlayerHand().remove(action.getHandIdx());
                                    player.useMana(card.getMana());
                                }
                            }
                            break;

                        case "getCardsInHand":
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
                            break;

                        case "getCardsOnTable":
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
                            break;

                        case "getPlayerMana":
                            ObjectNode getPlayerManaNode = mapper.createObjectNode();
                            getPlayerManaNode.put("command", action.getCommand());
                            getPlayerManaNode.put("playerIdx", action.getPlayerIdx());
                            if (action.getPlayerIdx() == 1) {
                                getPlayerManaNode.put("output", playerOne.getManaLeftToUse());
                            } else {
                                getPlayerManaNode.put("output", playerTwo.getManaLeftToUse());
                            }
                            output.add(getPlayerManaNode);
                            break;

                        case "cardUsesAttack":
                            int xAttacker = action.getCardAttacker().getX();
                            int yAttacker = action.getCardAttacker().getY();
                            MinionCard attackerCard = table.getCard(xAttacker, yAttacker);

                            int xAttacked = action.getCardAttacked().getX();
                            int yAttacked = action.getCardAttacked().getY();
                            MinionCard attackedCard = table.getCard(xAttacked, yAttacked);

                            if (attackerCard != null && attackedCard != null) {
                                if ((xAttacker < 2 && xAttacked < 2) || (xAttacker > 1 && xAttacked > 1)) {
                                    ObjectNode errorCardNode = mapper.createObjectNode();
                                    errorCardNode.put("command", action.getCommand());
                                    ArrayNode attackerCardNode = mapper.createArrayNode();
                                    attackerCardNode.add(mapper.convertValue(attackerCard, ObjectNode.class));
                                    errorCardNode.set("cardAttacker", attackerCardNode);
                                    ArrayNode attackedCardNode = mapper.createArrayNode();
                                    attackedCardNode.add(mapper.convertValue(attackedCard, ObjectNode.class));
                                    errorCardNode.set("cardAttacked", attackedCardNode);
                                    errorCardNode.put("error", "Attacked card does not belong to the enemy.");
                                    output.add(errorCardNode);
                                    break;
                                } else if (attackedCard.getAttacked() == 1) {
                                    ObjectNode errorCardNode = mapper.createObjectNode();
                                    errorCardNode.put("command", action.getCommand());
                                    ArrayNode attackerCardNode = mapper.createArrayNode();
                                    attackerCardNode.add(mapper.convertValue(attackerCard, ObjectNode.class));
                                    errorCardNode.set("cardAttacker", attackerCardNode);
                                    ArrayNode attackedCardNode = mapper.createArrayNode();
                                    attackedCardNode.add(mapper.convertValue(attackedCard, ObjectNode.class));
                                    errorCardNode.set("cardAttacked", attackedCardNode);
                                    errorCardNode.put("error", "Attacker card has already attacked this turn.");
                                    output.add(errorCardNode);
                                    break;
                                } else if (attackerCard.getIsFrozen() == 1) {
                                        ObjectNode errorCardNode = mapper.createObjectNode();
                                        errorCardNode.put("command", action.getCommand());
                                        ArrayNode attackerCardNode = mapper.createArrayNode();
                                        attackerCardNode.add(mapper.convertValue(attackerCard, ObjectNode.class));
                                        errorCardNode.set("cardAttacker", attackerCardNode);
                                        ArrayNode attackedCardNode = mapper.createArrayNode();
                                        attackedCardNode.add(mapper.convertValue(attackedCard, ObjectNode.class));
                                        errorCardNode.set("cardAttacked", attackedCardNode);
                                        errorCardNode.put("error", "Attacker card is frozen.");
                                        output.add(errorCardNode);
                                } else if (!table.getTankCoordinates().isEmpty()) {
                                    if (!table.getTankCoordinates().contains(action.getCardAttacked())) {
                                        ObjectNode errorCardNode = mapper.createObjectNode();
                                        errorCardNode.put("command", action.getCommand());
                                        ArrayNode attackerCardNode = mapper.createArrayNode();
                                        attackerCardNode.add(mapper.convertValue(attackerCard, ObjectNode.class));
                                        errorCardNode.set("cardAttacker", attackerCardNode);
                                        ArrayNode attackedCardNode = mapper.createArrayNode();
                                        attackedCardNode.add(mapper.convertValue(attackedCard, ObjectNode.class));
                                        errorCardNode.set("cardAttacked", attackedCardNode);
                                        errorCardNode.put("error", "Attacked card is not of type 'Tank’.");
                                        output.add(errorCardNode);
                                    }
                                } else {
                                    int err = attackerCard.useAttackOnCard(attackedCard);
                                    if (err == 1) {
                                        table.removeCard(xAttacked, yAttacked);
                                    }
                                }
                            }
                            break;

                        default:
                            System.out.println("Unknown command: " + action.getCommand());
                            break;
                    }
                   if (roundIsFinished == 1) {
                       break;
                   }
                }
                if (actions.isEmpty()) {
                    break;
                }
            }


        };
        //objectNode.put("command", inputData.getPlayerOneDecks().getNrDecks());
        //objectNode.put("playerIdx", 2);

        //ArrayNode arrayNode = mapper.createArrayNode();
        //arrayNode.add(objectNode);

        //output.add(objectNode);

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), output);
    }
}
