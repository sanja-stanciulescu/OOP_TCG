package org.poo.cards;

import org.poo.fileio.CardInput;

import java.util.ArrayList;

public class Card {
    protected int mana;
    protected String description;
    protected ArrayList<String> colors;
    protected String name;

    public Card() {
    }

    public Card(final CardInput card) {
        this.name = card.getName();
        this.description = card.getDescription();
        this.colors = card.getColors();
        this.mana = card.getMana();
    }

    /**
     * Retrieves the current mana value.
     *
     * @return the mana value
     */
    public int getMana() {
        return mana;
    }

    /**
     * Sets the mana value.
     *
     * @param mana the new mana value to set
     */
    public void setMana(final int mana) {
        this.mana = mana;
    }

    /**
     * Retrieves the description.
     *
     * @return the description string
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description string to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Retrieves the list of colors.
     *
     * @return an ArrayList of color strings
     */
    public ArrayList<String> getColors() {
        return colors;
    }

    /**
     * Sets the list of colors.
     *
     * @param colors an ArrayList of color strings to set
     */
    public void setColors(final ArrayList<String> colors) {
        this.colors = colors;
    }

    /**
     * Retrieves the name.
     *
     * @return the name string
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name string to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Executes an attack on another card.
     */
    public void useAttackOnCard() {
    }

    /**
     * Activates the card's ability.
     */
    public void useAbility() {
    }

    /**
     * Executes an attack on the hero.
     */
    public void useAttackOnHero() {
    }

    /**
     * Returns a string representation of the card, including its mana, description,
     * colors, and name.
     *
     * @return a formatted string with the card's attributes
     */
    public String toString() {
        return "Card{"
                +  "mana="
                + mana
                +  ", description='"
                + description
                + '\''
                + ", colors="
                + colors
                + ", name='"
                +  ""
                + name
                + '\''
                + '}';
    }
}

