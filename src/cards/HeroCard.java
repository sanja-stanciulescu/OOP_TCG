package cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fileio.CardInput;
import table.GameTable;

public class HeroCard extends Card{
    private int health;

    @JsonIgnore
    int attacked;

    public HeroCard() {
    }

    public HeroCard(CardInput playerHero) {
        super(playerHero);
        health = 30;
        attacked = 0;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getAttacked() {
        return attacked;
    }

    public void setAttacked(int attacked) {
        this.attacked = attacked;
    }

    public void loseHealthAfterAttack(int attackDamage) {
        health -= attackDamage;
    }


    public void useAbility(GameTable table, int affectedRow) {
        attacked = 1;
        switch (name) {
            case "Lord Royce":
                table.freezes(affectedRow);
                break;
            case "Empress Thorina":
                table.removeHealthiestCard(affectedRow);
                break;
            case "King Mudface":
                table.injectHealth(affectedRow);
                break;
            case "General Kocioraw":
                table.buffAttack(affectedRow);
                break;
            default:
                System.out.println("Invalid hero name");
                break;
        }
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
