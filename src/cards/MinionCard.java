package cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fileio.CardInput;

public class MinionCard extends Card{
    private int health;
    private int attackDamage;

    @JsonIgnore
    private int attacked;
    @JsonIgnore
    private int isFrozen;

    public MinionCard() {
    }

    public MinionCard(CardInput card) {
       super(card);
       health = card.getHealth();
       attackDamage = card.getAttackDamage();
       attacked = 0;
       isFrozen = 0;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public int getIsFrozen() {
        return isFrozen;
    }

    public void setIsFrozen(int isFrozen) {
        this.isFrozen = isFrozen;
    }

    public int getAttacked() {
        return attacked;
    }

    public void setAttacked(int attacked) {
        this.attacked = attacked;
    }

    public int determineRow(int playerIdx) {
        if (name.equals("Sentinel") || name.equals("Berserker") || name.equals("The Cursed One") || name.equals("Disciple")) {
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

    public void isHitByAttack(int attackDamage) {
        health -= attackDamage;
    }

    public int useAttackOnCard(MinionCard attackedCard) {
        attackedCard.isHitByAttack(attackDamage);
        attacked = 1;
        if (attackedCard.getHealth() <= 0) {
            return 1;
        }
        return 0;
    }

    @Override
    public void useAbility() {

    }

    @Override
    public void useAttackOnHero() {

    }

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
