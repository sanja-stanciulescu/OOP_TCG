package cards;

import fileio.CardInput;

import java.util.ArrayList;

public class Card {
    protected int mana;
    protected String description;
    protected ArrayList<String> colors;
    protected String name;

    public Card() {
    }

    public Card(CardInput card) {
        this.name = card.getName();
        this.description = card.getDescription();
        this.colors = card.getColors();
        this.mana = card.getMana();
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public void setColors(ArrayList<String> colors) {
        this.colors = colors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void useAttackOnCard() {
    }

    public void useAbility() {
    }

    public void useAttackOnHero() {
    }

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
