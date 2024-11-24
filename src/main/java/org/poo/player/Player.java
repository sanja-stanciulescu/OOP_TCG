package org.poo.player;

import org.poo.cards.MinionCard;
import org.poo.cards.HeroCard;

import java.util.ArrayList;

public class Player {
    private int playerIdx;
    private int manaLeftToUse;
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
        this.playerDeck = playerDeck;
        this.playerHero = playerHero;
        this.playerHand = new ArrayList<>();
    }

    /**
     * This method is used to add cards into the player's hand.
     */
    public void addCardToHand() {
        if (playerHand != null && !playerDeck.isEmpty()) {
            MinionCard card = playerDeck.removeFirst();
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
     * Gets the index of the player.
     *
     * @return the index of the player.
     */
    public int getPlayerIdx() {
        return playerIdx;
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
     * Gets the hero card associated with the player.
     *
     * @return the player's hero card.
     */
    public HeroCard getPlayerHero() {
        return playerHero;
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
     * Gets the deck of minion cards for the player.
     *
     * @return the list of minion cards in the player's deck.
     */
    public ArrayList<MinionCard> getPlayerDeck() {
        return playerDeck;
    }

}
