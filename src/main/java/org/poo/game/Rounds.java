package org.poo.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.cards.MinionCard;
import org.poo.fileio.ActionsInput;
import org.poo.fileio.StartGameInput;
import org.poo.player.Player;
import org.poo.table.GameTable;


import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Rounds {
    private int manaPerRound;
    private ArrayList<Player> turns;

    public Rounds() {
        turns = new ArrayList<>(2);
        manaPerRound = 0;
    }

    /**
     * Starts the game and controls the flow of the rounds, managing the players, their mana,
     * and actions.
     * <p>
     * This method initializes the order of players based on the starting player, then enters
     * a loop where it simulates the rounds of the game until one of the players' heroes is
     * defeated.
     * During each round, it increments mana, draws a card for each player, and processes
     * actions specified in the {@link ActionsInput} array.
     *
     * @param output an {@link ArrayNode} that stores the game output such as the result of actions
     * @param startGame an instance of {@link StartGameInput} containing the game setup, including
     * the starting player and other settings
     * @param actions an {@link ArrayList} of {@link ActionsInput} representing the list of actions
     * to be performed during the game
     * @param table the current {@link GameTable} representing the state of the game board
     * @param playerOne the first {@link Player} in the game
     * @param playerTwo the second {@link Player} in the game
     * @param gamesPlayerOneWon an {@link AtomicInteger} tracking the number of games won
     * by player one
     * @param gamesPlayerTwoWon an {@link AtomicInteger} tracking the number of games won
     * by player two
     * @param gamesPlayed an integer representing the total number of games played
     */
    public void start(
            final ArrayNode output,
            final StartGameInput startGame,
            final ArrayList<ActionsInput> actions,
            final GameTable table,
            final Player playerOne,
            final Player playerTwo,
            final AtomicInteger gamesPlayerOneWon,
            final AtomicInteger gamesPlayerTwoWon,
            final int gamesPlayed
    ) {
        //Initialize order of players in turns
        if (startGame.getStartingPlayer() == 1) {
            turns.add(playerOne);
            turns.add(playerTwo);
        } else {
            turns.add(playerTwo);
            turns.add(playerOne);
        }

        Commands command = new Commands(0);

        //Start playing rounds
        while (playerOne.getPlayerHero().getHealth() > 0
                && playerTwo.getPlayerHero().getHealth() > 0) {
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
                ActionsInput action = actions.removeFirst();
                roundIsFinished = actOnCommand(
                        output,
                        startGame,
                        action,
                        command,
                        table,
                        playerOne,
                        playerTwo,
                        gamesPlayerOneWon,
                        gamesPlayerTwoWon,
                        gamesPlayed
                );
                if (roundIsFinished == 1) {
                    break;
                }
            }
            if (actions.isEmpty()) {
                break;
            }
        }
    }

    /**
     * Processes a specific command during the game based on the provided action input.
     * <p>
     * This method handles various game commands, such as retrieving player information,
     * managing the deck, placing cards, attacking, and using abilities. It executes the
     * appropriate action based on the command in the {@link ActionsInput} and modifies
     * the game state accordingly.
     *
     * @param output an {@link ArrayNode} where the results of the action
     * will be stored and returned
     * @param startGame an instance of {@link StartGameInput} containing the
     * start game parameters
     * @param action an {@link ActionsInput} object containing the command to be executed
     * @param table the current {@link GameTable} representing the state of the game
     * @param playerOne the first {@link Player} in the game
     * @param playerTwo the second {@link Player} in the game
     * @param gamesPlayerOneWon an {@link AtomicInteger} tracking the number of games won
     * by player one
     * @param gamesPlayerTwoWon an {@link AtomicInteger} tracking the number of games won
     * by player two
     * @param gamesPlayed an integer representing the total number of games played
     * @return an integer indicating whether the round is finished (1 if finished, 0 otherwise)
     */
    public int actOnCommand(
            final ArrayNode output,
            final StartGameInput startGame,
            final ActionsInput action,
            final Commands command,
            final GameTable table,
            final Player playerOne,
            final Player playerTwo,
            final AtomicInteger gamesPlayerOneWon,
            final AtomicInteger gamesPlayerTwoWon,
            final int gamesPlayed
    ) {
        //Player whose turn is now
        Player player = turns.get(command.getCurrentTurn() % 2);

        ArrayList<MinionCard> deckPlayerOne = playerOne.getPlayerDeck();
        ArrayList<MinionCard> deckPlayerTwo = playerTwo.getPlayerDeck();

        ObjectMapper mapper = new ObjectMapper();
        int roundIsFinished = 0;
        switch (action.getCommand()) {
            case "getPlayerDeck":
                command.getPlayerDeck(output, action, deckPlayerOne, deckPlayerTwo);
                break;

            case "getPlayerHero":
                command.getPlayerHero(output, action, playerOne, playerTwo);
                break;

            case "getPlayerTurn":
                command.getPlayerTurn(output, action, startGame);
                break;

            case "endPlayerTurn":
                roundIsFinished = command.endPlayerTurn(table, player, playerOne, playerTwo);
                break;

            case "placeCard":
                command.placeCard(output, player, action, table);
                break;

            case "getCardsInHand":
                command.getCardsInHand(output, action, playerOne, playerTwo);
                break;

            case "getCardsOnTable":
                command.getCardsOnTable(output, action, table);
                break;

            case "getCardAtPosition":
                command.getCardAtPosition(output, action, table);
                break;

            case "getPlayerMana":
                command.getPlayerMana(output, action, playerOne, playerTwo);
                break;

            case "cardUsesAttack":
                command.cardUsesAttack(output, action, player, table);
                break;

            case "cardUsesAbility":
                command.cardUsesAbility(output, action, player, table);
                break;

            case "useAttackHero":
                command.useAttackHero(
                        output,
                        action,
                        player,
                        playerOne,
                        playerTwo,
                        table,
                        gamesPlayerOneWon,
                        gamesPlayerTwoWon
                );
                break;

            case "useHeroAbility":
                command.useHeroAbility(output, action, player, table);
                break;

            case "getFrozenCardsOnTable":
                command.getFrozenCardsOnTable(output, action, table);
                break;

            case "getPlayerOneWins":
                command.getPlayerOneWins(output, action, gamesPlayerOneWon);
                break;

            case "getPlayerTwoWins":
                command.getPlayerTwoWins(output, action, gamesPlayerTwoWon);
                break;

            case "getTotalGamesPlayed":
                command.getTotalGamesPlayed(output, action, gamesPlayed);
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

}
