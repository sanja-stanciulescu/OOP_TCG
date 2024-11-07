package game;

import cards.HeroCard;
import cards.MinionCard;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.*;
import player.Player;
import table.GameTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class GameManager {
    public int gamesPlayed;
    public AtomicInteger gamesPlayerOneWon;
    public AtomicInteger gamesPlayerTwoWon;

    public GameManager() {
        gamesPlayed = 0;
        gamesPlayerOneWon = new AtomicInteger(0);
        gamesPlayerTwoWon = new AtomicInteger(0);
    }

    public void multipleGames(ArrayNode output, ArrayNode objectNode, Input inputData) {
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
            int manaPerRound = 0;
            int currentTurn = 0;
            GameTable table = new GameTable();
            Rounds round = new Rounds();

            round.start(output, startGame, actions, table, playerOne, playerTwo, gamesPlayerOneWon, gamesPlayerTwoWon, gamesPlayed);

        };
    }

    public Player initializePlayerOne(Input inputData, StartGameInput startGame) {
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

    public Player initializePlayerTwo(Input inputData, StartGameInput startGame) {
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
