package org.poo.table;

import org.poo.cards.MinionCard;
import org.poo.fileio.Coordinates;

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

    /**
     * Retrieves the coordinates of tank cards for a specific player.
     *
     * @param index the index of the player whose tank coordinates are being retrieved.
     * @return a list of {@link Coordinates} objects representing the tank card positions
     * for the player.
     */
    public ArrayList<Coordinates> getTankCoordinates(final int index) {
        return tankCoordinates.get(index);
    }

    /**
     * Places a card on the game table at a specific row and updates tank coordinates
     * if the card is a tank.
     *
     * @param index the index of the player placing the card.
     * @param row   the row on the table where the card should be placed (0-3).
     * @param card  the {@link MinionCard} to be placed.
     */
    public void placeCard(final int index, final int row, final MinionCard card) {
        if (row >= 0 && row < 4) {
            table.get(row).add(card);
        }
        if (card.getName().equals("Goliath") || card.getName().equals("Warden")) {
            Coordinates coordinates = new Coordinates();
            coordinates.setX(row);
            coordinates.setY(table.get(row).size() - 1);
            tankCoordinates.get(index).add(coordinates);
        }
    }

    /**
     * Removes a card from the game table at the specified position.
     *
     * @param row the row on the table where the card is located (0-3).
     * @param col the column (position) of the card within the row.
     */
    public void removeCard(final int row, final int col) {
        if (row >= 0 && row < 4 && col >= 0 && col < table.get(row).size()) {
            table.get(row).remove(col);
        }
    }

    /**
     * Retrieves the card located at the specified position on the table.
     *
     * @param row the row on the table where the card is located (0-3).
     * @param col the column (position) of the card within the row.
     * @return the {@link MinionCard} at the specified position,
     * or {@code null} if the position is invalid.
     */
    public MinionCard getCard(final int row, final int col) {
        if (row >= 0 && row < 4 && col >= 0 && col < table.get(row).size()) {
            return table.get(row).get(col);
        }
        return null;
    }

    /**
     * Checks if there is space available on the specified row to add more cards.
     *
     * @param row the row on the table to check (0-3).
     * @return {@code true} if the row has fewer than 5 cards, {@code false} otherwise.
     */
    public boolean isSpaceOnRow(final int row) {
        return table.get(row).size() < 5;
    }

    /**
     * Checks if a tank card exists at the specified coordinates for a given player.
     *
     * @param x the row coordinate of the card.
     * @param y the column coordinate of the card.
     * @param index the index of the player whose tank coordinates are being checked.
     * @return the index of the tank in the player's tank coordinate list,
     * or {@code -1} if no tank exists at the given position.
     */
    public int checkTanks(final int x, final int y, final int index) {
        int i;
        for (i = 0; i < tankCoordinates.get(index).size(); ++i) {
            if (tankCoordinates.get(index).get(i).getX() == x
                    && tankCoordinates.get(index).get(i).getY() == y) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Searches for and removes a tank card from the tank coordinates
     * if it exists at the specified position.
     *
     * @param row the row coordinate of the tank card.
     * @param col the column coordinate of the tank card.
     */
    public void searchAndRemoveTank(final int row, final int col) {
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

    /**
     * Updates the tank coordinates when a tank card is removed or shifted.
     *
     * @param index the index of the player whose tank coordinates are being updated.
     * @param i     the index of the tank card in the player's tank coordinate list to update.
     */
    public void updateTankCoord(final int index, final int i) {
        ArrayList<Coordinates> rowCoordinates = tankCoordinates.get(index);
        for (Coordinates coordinates : rowCoordinates) {
            if (coordinates.getX() == tankCoordinates.get(index).get(i).getX()) {
                if (coordinates.getY() > tankCoordinates.get(index).get(i).getY()) {
                    int y = coordinates.getY();
                    coordinates.setY(coordinates.getY() - 1);
                }
            }
        }
    }

    /**
     * Removes a tank card from the tank coordinates and updates the remaining coordinates.
     *
     * @param x     the row coordinate of the tank card.
     * @param y     the column coordinate of the tank card.
     * @param index the index of the player whose tank card is being removed.
     */
    public void removeTank(final int x, final int y, final int index) {
        int i = checkTanks(x, y, index);
        if (i != -1) {
            updateTankCoord(index, i);
            tankCoordinates.get(index).remove(i);
        }
    }

    /**
     * Freezes all cards on a specified row.
     *
     * @param row the row of the table to freeze (0-3).
     */
    public void freezes(final int row) {
        for (int i = 0; i < table.get(row).size(); ++i) {
            MinionCard card = table.get(row).get(i);
            card.setIsFrozen(1);
        }
    }

    /**
     * Removes the card with the highest health from a specified row. If the card is a tank,
     * it also updates and removes the tank's coordinates if necessary.
     *
     * @param row the row on the table from which to remove the healthiest card.
     */
    public void removeHealthiestCard(final int row) {
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

    /**
     * Increases the health of all cards on a specified row by 1.
     *
     * @param row the row of the table to inject health into (0-3).
     */
    public void injectHealth(final int row) {
        for (int i = 0; i < table.get(row).size(); ++i) {
            MinionCard card = table.get(row).get(i);
            int health = card.getHealth();
            card.setHealth(health + 1);
        }
    }

    /**
     * Increases the attack damage of all cards on a specified row by 1.
     *
     * @param row the row of the table to buff attack on (0-3).
     */
    public void buffAttack(final int row) {
        for (int i = 0; i < table.get(row).size(); ++i) {
            MinionCard card = table.get(row).get(i);
            int attack = card.getAttackDamage();
            card.setAttackDamage(attack + 1);
        }
    }
}

