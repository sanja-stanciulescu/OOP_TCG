package cards;

import fileio.CardInput;

import java.util.ArrayList;

public class HeroCard extends Card{
    private int health;

    public HeroCard() {
    }

    public HeroCard(CardInput playerHero) {
        super(playerHero);
        health = 30;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void loseHealthAfterAttack(int attackDamage) {
        health -= attackDamage;
    }

    @Override
    public void useAbility() {
        System.out.println("Hero uses ability on row");
    }

    @Override
    public String toString() {
        return "Hero{"
                +  "mana="
                + mana
                + ", health="
                + health
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
