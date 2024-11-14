package cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fileio.CardInput;
import table.GameTable;

public class HeroCard extends Card {
    private int health;

    @JsonIgnore
    private int attacked;

    public HeroCard() {
    }

    public HeroCard(final CardInput playerHero) {
        super(playerHero);
        this.setHealth(30);
        attacked = 0;
    }

    /**
     * Retrieves the current health value.
     *
     * @return the health value
     */
    public int getHealth() {
        return health;
    }

    /**
     * Sets the health value.
     *
     * @param health the new health value to set
     */
    public void setHealth(final int health) {
        this.health = health;
    }

    /**
     * Retrieves the attack status.
     *
     * @return the attack status
     */
    public int getAttacked() {
        return attacked;
    }

    /**
     * Sets the attack status.
     *
     * @param attacked the new attack status to set
     */
    public void setAttacked(final int attacked) {
        this.attacked = attacked;
    }

    /**
     * Reduces the health based on the damage taken from an attack.
     *
     * @param attackDamage the amount of damage to subtract from health
     */
    public void loseHealthAfterAttack(final int attackDamage) {
        health -= attackDamage;
    }

    /**
     * Uses the hero's special ability on a specified row of the game table.
     * The ability used depends on the hero's name:
     * <ul>
     *     <li><strong>Lord Royce</strong> - Freezes all cards in the specified row.</li>
     *     <li><strong>Empress Thorina</strong> - Removes the card with the highest health in
     * the specified row.</li>
     *     <li><strong>King Mudface</strong> - Heals all cards in the specified row.</li>
     *     <li><strong>General Kocioraw</strong> - Increases the attack of all cards in the
     * specified row.</li>
     *     <li>Prints an error message if the hero's name is invalid.</li>
     * </ul>
     *
     * @param table the game table on which the ability is used
     * @param affectedRow the row on the table that the ability affects
     */
    public void useAbility(final GameTable table, final int affectedRow) {
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

    /**
     * Returns a string representation of the hero, including its mana, health,
     * description, colors, and name.
     *
     * @return a formatted string with the hero's attributes
     */
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
