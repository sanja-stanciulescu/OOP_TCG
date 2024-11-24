package org.poo.game;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.cards.HeroCard;
import org.poo.cards.MinionCard;
import org.poo.fileio.*;
import org.poo.player.Player;
import org.poo.table.GameTable;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The GameManager class is responsible for managing the overall flow of the org.poo.game,
 * including initializing players, handling multiple games, and tracking org.poo.game statistics.
 */
public class GameManager {
    private int gamesPlayed;
    private AtomicInteger gamesPlayerOneWon;
    private AtomicInteger gamesPlayerTwoWon;

    public GameManager() {
        gamesPlayed = 0;
        gamesPlayerOneWon = new AtomicInteger(0);
        gamesPlayerTwoWon = new AtomicInteger(0);
    }

    /**
     * Plays multiple games based on the provided input data and outputs the results.
     *
     * @param output         An ArrayNode to store the output of each action in a org.poo.game.
     * @param objectNode     An ArrayNode object used in the org.poo.game processing.
     * @param inputData      The input data containing all relevant information.
     */
    public void multipleGames(
            final ArrayNode output,
            final ArrayNode objectNode,
            final Input inputData
    ) {
        ArrayList<GameInput> games = inputData.getGames();

        for (GameInput game : games) {
            gamesPlayed++;
            int gameEnded = 0;
            StartGameInput startGame = game.getStartGame();
            ArrayList<ActionsInput> actions = game.getActions();

            //Initialize the two players:
            Player playerOne = initializePlayerOne(inputData, startGame);
            Player playerTwo = initializePlayerTwo(inputData, startGame);

            //Initializes the start of the rounds
            GameTable table = new GameTable();
            Rounds round = new Rounds();

            round.start(
                    output,
                    startGame,
                    actions,
                    table,
                    playerOne,
                    playerTwo,
                    gamesPlayerOneWon,
                    gamesPlayerTwoWon,
                    gamesPlayed
            );
        }
    }

    /**
     * Initializes and returns the first player with a deck and a hero card.
     * <p>
     * The method performs the following steps:
     * <ol>
     *     <li>Picks the chosen deck based on the index provided in the org.poo.game input.</li>
     *     <li>Transforms the chosen deck into a list of {@link MinionCard} objects.</li>
     *     <li>Shuffles the deck using a random seed specified in the org.poo.game input.</li>
     *     <li>Assigns a hero card to the player based on the org.poo.game input.</li>
     * </ol>
     *
     * @param inputData an instance of {@link Input} containing available deck data
     * @param startGame an instance of {@link StartGameInput} containing org.poo.game-specific
     * settings such as the chosen deck index and shuffle seed
     * @return a {@link Player} object representing player one, with the shuffled deck
     * and assigned hero card
     */
    private Player initializePlayerOne(final Input inputData, final StartGameInput startGame) {
        //1. Pick chosen deck
        ArrayList<CardInput> deckOne = new ArrayList<>(
                inputData.getPlayerOneDecks()
                        .getDecks()
                        .get(startGame.getPlayerOneDeckIdx())
        );

        //2. Give deck to the players
        ArrayList<MinionCard> deckPlayerOne = new ArrayList<MinionCard>();

        for (CardInput card : deckOne) {
            MinionCard minionCard = new MinionCard(card);
            deckPlayerOne.add(minionCard);
        }

        //3. Shuffle chosen deck
        Random rand1 = new Random(startGame.getShuffleSeed());
        Collections.shuffle(deckPlayerOne, rand1);

        //4. Give hero card to the players
        CardInput heroInput = startGame.getPlayerOneHero();
        HeroCard playerOneHero = new HeroCard(heroInput);

        return new Player(1, deckPlayerOne, playerOneHero);
    }

    /**
     * Initializes and returns the second player with a deck and a hero card.
     * <p>
     * The method performs the following steps:
     * <ol>
     *     <li>Picks the chosen deck based on the index provided in the org.poo.game input.</li>
     *     <li>Transforms the chosen deck into a list of {@link MinionCard} objects.</li>
     *     <li>Shuffles the deck using a random seed specified in the org.poo.game input.</li>
     *     <li>Assigns a hero card to the player based on the org.poo.game input.</li>
     * </ol>
     *
     * @param inputData an instance of {@link Input} containing available deck data
     * @param startGame an instance of {@link StartGameInput} containing org.poo.game-specific
     * settings such as the chosen deck index and shuffle seed
     * @return a {@link Player} object representing player one, with the shuffled deck
     * and assigned hero card
     */
    private Player initializePlayerTwo(final Input inputData, final StartGameInput startGame) {
        //1. Pick chosen deck
        ArrayList<CardInput> deckTwo = new ArrayList<>(
                inputData.getPlayerTwoDecks()
                        .getDecks()
                        .get(startGame.getPlayerTwoDeckIdx())
        );

        //2. Give deck to the players
        ArrayList<MinionCard> deckPlayerTwo = new ArrayList<MinionCard>();

        for (CardInput card : deckTwo) {
            MinionCard minionCard = new MinionCard(card);
            deckPlayerTwo.add(minionCard);
        }

        //3. Shuffle chosen deck
        Random rand2 = new Random(startGame.getShuffleSeed());
        Collections.shuffle(deckPlayerTwo, rand2);

        //4. Give hero card to the players
        CardInput heroInput = startGame.getPlayerTwoHero();
        HeroCard playerTwoHero = new HeroCard(heroInput);

        return new Player(2, deckPlayerTwo, playerTwoHero);
    }
}
