package table;

import cards.MinionCard;
import fileio.Coordinates;

import java.util.ArrayList;
import java.util.Iterator;

public class GameTable {
    private ArrayList<ArrayList<MinionCard>> table;
    private ArrayList<ArrayList<Coordinates>> tankCoordinates;

    public GameTable() {
        table = new ArrayList<>(4);
        for (int i = 0; i < 4; ++i) {
            ArrayList<MinionCard> row = new ArrayList<>();
            table.add(row);
        }
        tankCoordinates = new ArrayList<ArrayList<Coordinates>>(2);
        for (int i = 0; i < 2; ++i) {
            ArrayList<Coordinates> row = new ArrayList<>();
            tankCoordinates.add(row);
        }
    }

    public ArrayList<Coordinates> getTankCoordinates(int index) {
        return tankCoordinates.get(index);
    }

    public void placeCard(int index, int row, MinionCard card) {
        if (row >= 0 && row < 4) {
            table.get(row).add(card);
        }
        if (card.getName().equals("Goliath") || card.getName().equals("Warden")) {
            Coordinates coordinates = new Coordinates(row, table.get(row).size() - 1);
            tankCoordinates.get(index).add(coordinates);
        }
    }

    public void removeCard(int row, int col) {
        if (row >= 0 && row < 4 && col >= 0 && col < table.get(row).size()) {
            table.get(row).remove(col);
        }
    }

    public MinionCard getCard(int row, int col) {
        if (row >= 0 && row < 4 && col >= 0 && col < table.get(row).size()) {
            return table.get(row).get(col);
        }
        return null;
    }

    public boolean isSpaceOnRow(int row) {
        //System.out.println("Row size is " + table.get(row).size());
        if (table.get(row).size() < 5)
            return true;
        return false;
    }

    public int checkTanks(int x, int y, int index) {
        int i;
        for(i = 0; i < tankCoordinates.get(index).size(); ++i) {
            if (tankCoordinates.get(index).get(i).getX() == x && tankCoordinates.get(index).get(i).getY() == y) {
                return i;
            }
        }
        return -1;
    }

    public void searchAndRemoveTank(int row, int col) {
        for (ArrayList<Coordinates> rowCoordinates : tankCoordinates) {
            Iterator<Coordinates> iterator = rowCoordinates.iterator();
            while (iterator.hasNext()) {
                Coordinates coordinates = iterator.next();
                if (coordinates.getX() == row && coordinates.getY() == col) {
                    iterator.remove();
                }
            }
        }
    }

    public void removeTank(int x, int y, int index) {
        int i = checkTanks(x, y, index);
        if (i != -1) {
            tankCoordinates.get(index).remove(i);
        }
    }

    public void freezes(int row) {
        for (int i = 0; i < table.get(row).size(); ++i) {
            MinionCard card = table.get(row).get(i);
            card.setIsFrozen(1);
        }
    }

    public void removeHealthiestCard(int row) {
        int maxHealth = 0;
        int index = 0;
        MinionCard card;
        for (int i = 0; i < table.get(row).size(); ++i) {
             card = table.get(row).get(i);
            if (card.getHealth() > maxHealth) {
                maxHealth = card.getHealth();
                index = i;
            }
        }
        card = table.get(row).get(index);
        if (card.getName().equals("Goliath") || card.getName().equals("Warden")) {
            searchAndRemoveTank(row, index);
        }
        table.get(row).remove(index);
    }

    public void injectHealth(int row) {
        for (int i = 0; i < table.get(row).size(); ++i) {
            MinionCard card = table.get(row).get(i);
            int health = card.getHealth();
            card.setHealth(health + 1);
        }
    }

    public void buffAttack(int row) {
        for (int i = 0; i < table.get(row).size(); ++i) {
            MinionCard card = table.get(row).get(i);
            int attack = card.getAttackDamage();
            card.setAttackDamage(attack + 1);
        }
    }
}
