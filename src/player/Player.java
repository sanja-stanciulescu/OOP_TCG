package player;

import cards.MinionCard;
import cards.HeroCard;

import java.util.ArrayList;

public class Player {
    private int playerIdx;
    private int backRow;
    private int frontRow;
    private int manaLeftToUse;
    private int gamesWon;
    private ArrayList<MinionCard> playerDeck;
    private HeroCard playerHero;
    private ArrayList<MinionCard> playerHand;

    public Player() {
    }

    public Player(
            final int playerIdx,
            final ArrayList<MinionCard> playerDeck,
            final HeroCard playerHero
    ) {
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

    /**
     * Gets the index of the player.
     *
     * @return the index of the player.
     */
    public int getPlayerIdx() {
        return playerIdx;
    }

    /**
     * Sets the index of the player.
     *
     * @param playerIdx the index to set for the player.
     */
    public void setPlayerIdx(final int playerIdx) {
        this.playerIdx = playerIdx;
    }

    /**
     * Gets the back row associated with the player.
     *
     * @return the back row.
     */
    public int getBackRow() {
        return backRow;
    }

    /**
     * Sets the back row for the player.
     *
     * @param backRow the value of the back row to set.
     */
    public void setBackRow(final int backRow) {
        this.backRow = backRow;
    }

    /**
     * Gets the front row associated with the player.
     *
     * @return the front row.
     */
    public int getFrontRow() {
        return frontRow;
    }

    /**
     * Sets the front row for the player.
     *
     * @param frontRow the value of the front row to set.
     */
    public void setFrontRow(final int frontRow) {
        this.frontRow = frontRow;
    }

    /**
     * Gets the number of games the player has won.
     *
     * @return the number of games won.
     */
    public int getGamesWon() {
        return gamesWon;
    }

    /**
     * Sets the number of games the player has won.
     *
     * @param gamesWon the number of games won to set.
     */
    public void setGamesWon(final int gamesWon) {
        this.gamesWon = gamesWon;
    }

    /**
     * Gets the amount of mana left to use for the player.
     *
     * @return the remaining mana.
     */
    public int getManaLeftToUse() {
        return manaLeftToUse;
    }

    /**
     * Sets the amount of mana left to use for the player.
     *
     * @param manaLeftToUse the amount of mana to set.
     */
    public void setManaLeftToUse(final int manaLeftToUse) {
        this.manaLeftToUse = manaLeftToUse;
    }

    /**
     * Gets the hero card associated with the player.
     *
     * @return the player's hero card.
     */
    public HeroCard getPlayerHero() {
        return playerHero;
    }

    /**
     * Sets the hero card for the player.
     *
     * @param playerHero the hero card to set.
     */
    public void setPlayerHero(final HeroCard playerHero) {
        this.playerHero = playerHero;
    }

    /**
     * Gets the hand of minion cards held by the player.
     *
     * @return the list of minion cards in the player's hand.
     */
    public ArrayList<MinionCard> getPlayerHand() {
        return playerHand;
    }

    /**
     * Sets the hand of minion cards for the player.
     *
     * @param playerHand the list of minion cards to set as the player's hand.
     */
    public void setPlayerHand(final ArrayList<MinionCard> playerHand) {
        this.playerHand = playerHand;
    }

    /**
     * Gets the deck of minion cards for the player.
     *
     * @return the list of minion cards in the player's deck.
     */
    public ArrayList<MinionCard> getPlayerDeck() {
        return playerDeck;
    }

    /**
     * Sets the deck of minion cards for the player.
     *
     * @param playerDeck the list of minion cards to set as the player's deck.
     */
    public void setPlayerDeck(final ArrayList<MinionCard> playerDeck) {
        this.playerDeck = playerDeck;
    }

    /**
    * This method is used to add cards into the player's hand.
    */
    public void addCardToHand() {
        if (playerHand != null && !playerDeck.isEmpty()) {
            MinionCard card = playerDeck.remove(0);
            playerHand.add(card);
        }
    }

    /**
     * This method is used when the player wants to put a card on the table.
     */
    public MinionCard checkCardInHand(final int handIdx) {
        if (!playerHand.isEmpty() && handIdx < playerHand.size()) {
            return playerHand.get(handIdx);
        }
        return null;
    }

    /**
     * This method is used for adding mana each new round.
     */
    public void addMana(final int mana) {
        manaLeftToUse += mana;
    }

    /**
     * This method is used when player consumed mana.
     */
    public void useMana(final int mana) {
        manaLeftToUse -= mana;
    }

    /**
     * This method is used when a player wins a game.
     */
    public void winsGame() {
        gamesWon++;
    }
}
