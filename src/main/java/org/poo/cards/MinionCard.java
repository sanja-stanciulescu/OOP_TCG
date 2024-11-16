package org.poo.cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.poo.fileio.CardInput;

public class MinionCard extends Card {
    private int health;
    private int attackDamage;

    @JsonIgnore
    private int attacked;
    @JsonIgnore
    private int isFrozen;

    public MinionCard() {
    }

    public MinionCard(final CardInput card) {
        super(card);
        health = card.getHealth();
        attackDamage = card.getAttackDamage();
        attacked = 0;
        isFrozen = 0;
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
     * Retrieves the current attack damage value.
     *
     * @return the attack damage value
     */
    public int getAttackDamage() {
        return attackDamage;
    }

    /**
     * Sets the attack damage. If the provided value is less than 0,
     * sets attack damage to 0 instead.
     *
     * @param attackDamage the attack damage value to set
     */
    public void setAttackDamage(final int attackDamage) {
        if (attackDamage < 0) {
            this.attackDamage = 0;
        } else {
            this.attackDamage = attackDamage;
        }
    }

    /**
     * Retrieves the freeze status.
     *
     * @return 1 if frozen, 0 otherwise
     */
    public int getIsFrozen() {
        return isFrozen;
    }

    /**
     * Sets the freeze status.
     *
     * @param isFrozen 1 if frozen, 0 otherwise
     */
    public void setIsFrozen(final int isFrozen) {
        this.isFrozen = isFrozen;
    }
    /**
     * Retrieves the attacked status.
     *
     * @return the attacked status value
     */
    public int getAttacked() {
        return attacked;
    }

    /**
     * Sets the attacked status.
     *
     * @param attacked the new attacked status value to set
     */
    public void setAttacked(final int attacked) {
        this.attacked = attacked;
    }

    /**
     * Determines the row position of the character based on the player's index.
     * Certain characters (Sentinel, Berserker, The Cursed One, Disciple) have specific rows:
     * <ul>
     *     <li>If player index is 1, returns row 3 for specific characters, or row 2 otherwise.</li>
     *     <li>If player index is 2, returns row 0 for specific characters, or row 1 otherwise.</li>
     * </ul>
     *
     * @param playerIdx the index of the player (1 or 2)
     * @return the determined row index
     */
    public int determineRow(final int playerIdx) {
        if (name.equals("Sentinel")
                || name.equals("Berserker")
                || name.equals("The Cursed One")
                || name.equals("Disciple")) {
            if (playerIdx == 1) {
                return 3;
            } else {
                return 0;
            }
        } else {
            if (playerIdx == 1) {
                return 2;
            } else {
                return 1;
            }
        }
    }

    /**
     * Reduces health based on the damage received from an attack.
     *
     * @param damage the amount of damage to subtract from health
     */
    public void isHitByAttack(final int damage) {
        health -= damage;
    }

    /**
     * Uses this minion's attack on another minion card.
     * Reduces the attacked card's health by this minion's attack damage.
     *
     * @param attackedCard the card being attacked
     * @return 1 if the attacked card's health drops to 0 or below, 0 otherwise
     */
    public int useAttackOnCard(final MinionCard attackedCard) {
        attackedCard.isHitByAttack(attackDamage);
        attacked = 1;
        if (attackedCard.getHealth() <= 0) {
            return 1;
        }
        return 0;
    }

    /**
     * Uses this minion's special ability on another minion card.
     * The ability applied depends on this minion's name:
     * <ul>
     *     <li><strong>The Ripper</strong> - Reduces the attacked card's attack
     * damage by 2.</li>
     *     <li><strong>Miraj</strong> - Swaps health values between this minion
     * and the attacked card.</li>
     *     <li><strong>The Cursed One</strong> - Swaps the health and attack damage
     * values of the attacked card.</li>
     *     <li><strong>Disciple</strong> - Increases the attacked card's health by 2.</li>
     *     <li>Prints an error message if the minion's name is invalid.</li>
     * </ul>
     *
     * @param attackedCard the card on which the ability is used
     * @return 1 if the attacked card's health drops to 0 or below, 0 otherwise
     */
    public int useAbility(final MinionCard attackedCard) {
        attacked = 1;
        switch (name) {
            case "The Ripper":
                int attackedCardDamage = attackedCard.getAttackDamage();
                attackedCard.setAttackDamage(attackedCardDamage - 2);
                break;

            case "Miraj":
                int healthCopy = health;
                health = attackedCard.getHealth();
                attackedCard.setHealth(healthCopy);
                break;

            case "The Cursed One":
                int attackCopy = attackedCard.getAttackDamage();
                healthCopy = attackedCard.getHealth();
                attackedCard.setHealth(attackCopy);
                attackedCard.setAttackDamage(healthCopy);
                break;

            case "Disciple":
                healthCopy = attackedCard.getHealth();
                attackedCard.setHealth(healthCopy + 2);
                break;

            default:
                System.out.println("Invalid minion name");
                break;
        }
        if (attackedCard.getHealth() <= 0) {
            return 1;
        }
        return 0;
    }

    /**
     * Uses this minion's attack on a hero.
     * Reduces the hero's health by this minion's attack damage.
     *
     * @param hero the hero being attacked
     * @return 1 if the hero's health drops to 0 or below, 0 otherwise
     */
    public int useAttackOnHero(final HeroCard hero) {
        attacked = 1;
        hero.loseHealthAfterAttack(attackDamage);
        if (hero.getHealth() <= 0) {
            return 1;
        }
        return 0;
    }

    /**
     * Returns a string representation of the minion, including its mana, attack damage,
     * health, description, colors, and name.
     *
     * @return a formatted string with the minion's attributes
     */
    @Override
    public String toString() {
        return "Minion{"
                +  "mana="
                + mana
                +  ", attackDamage="
                + attackDamage
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

