package main;

import checker.Checker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

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

        ArrayNode objectNode = mapper.createArrayNode();

        ArrayList<GameInput> games = inputData.getGames();
        for (GameInput game : games) {
            StartGameInput startGame = game.getStartGame();
            ArrayList<ActionsInput> actions = game.getActions();
            for (ActionsInput action : actions) {
                switch (action.getCommand()) {
                    case "getPlayerDeck":
                        ObjectNode getPlayerDeckNode = mapper.createObjectNode();
                        getPlayerDeckNode.put("command", action.getCommand());
                        getPlayerDeckNode.put("playerIdx", action.getPlayerIdx());
                        ArrayList<CardInput> deck;
                        if (action.getPlayerIdx() == 1) {
                            deck = inputData.getPlayerOneDecks().getDecks().get(startGame.getPlayerOneDeckIdx());
                        } else {
                            deck = inputData.getPlayerTwoDecks().getDecks().get(startGame.getPlayerTwoDeckIdx());
                        }
                        ArrayNode deckArray = mapper.createArrayNode();
                        for (CardInput card : deck) {
                            ObjectNode cardNode = mapper.convertValue(card, ObjectNode.class);
                            deckArray.add(cardNode);
                        }
                        getPlayerDeckNode.set("output", deckArray);
                        output.add(getPlayerDeckNode);
                        break;
                    default:
                        System.out.println("Unknown command: " + action.getCommand());
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
