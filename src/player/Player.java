package player;

import cards.*;

import java.util.ArrayList;

public class Player {
    int playerIdx;
    int backRow;
    int frontRow;
    private int manaLeftToUse;
    private int gamesWon;
    private ArrayList<MinionCard> playerDeck;
    private HeroCard playerHero;
    private ArrayList<MinionCard> playerHand;

    public Player() {}

    public Player(int playerIdx, ArrayList<MinionCard> playerDeck, HeroCard playerHero) {
        this.playerIdx = playerIdx;
        if (playerIdx == 1) {
            backRow = 3;
            frontRow = 2;
        } else {
            backRow = 0;
            frontRow = 1;
        }
        this.playerDeck = playerDeck;
        this.playerHero = playerHero;
        this.playerHand = new ArrayList<MinionCard>();
    }

    public int getPlayerIdx() {
        return playerIdx;
    }

    public void setPlayerIdx(int playerIdx) {
        this.playerIdx = playerIdx;
    }

    public int getBackRow() {
        return backRow;
    }

    public void setBackRow(int backRow) {
        this.backRow = backRow;
    }

    public int getFrontRow() {
        return frontRow;
    }

    public void setFrontRow(int frontRow) {
        this.frontRow = frontRow;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public int getManaLeftToUse() {
        return manaLeftToUse;
    }

    public void setManaLeftToUse(int manaLeftToUse) {
        this.manaLeftToUse = manaLeftToUse;
    }

    public HeroCard getPlayerHero() {
        return playerHero;
    }

    public void setPlayerHero(HeroCard playerHero) {
        this.playerHero = playerHero;
    }

    public ArrayList<MinionCard> getPlayerHand() {
        return playerHand;
    }

    public void setPlayerHand(ArrayList<MinionCard> playerHand) {
        this.playerHand = playerHand;
    }

    /*
    * This method is used to add cards into the player's hand
    */
    public void addCardToHand() {
        if (playerHand != null && !playerDeck.isEmpty()) {
            MinionCard card = playerDeck.remove(0);
            playerHand.add(card);
        }
    }

    /*
    * This method is used when the player wants to put a card on the table
     */
    public MinionCard putCardOnTable(int handIdx) {
        //System.out.println("HandIdx is " + handIdx + " and the hand size is " + playerHand.size());
        if (!playerHand.isEmpty() && handIdx < playerHand.size()) {
            return playerHand.get(handIdx);
        }
        //System.out.println("Something is wrong with the card idx");
        return null;
    }

    /*
    * This method is used for adding mana each new round
    */
    public void addMana(int mana) {
        manaLeftToUse += mana;
    }

    /*
    * This method is used when player consumed mana
     */
    public void useMana(int mana) {
        manaLeftToUse -= mana;
    }

    /*
    * This method is used when a player wins a game
    */
    public void winsGame() {
        gamesWon++;
    }
}
