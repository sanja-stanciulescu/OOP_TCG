package table;

import cards.MinionCard;
import fileio.Coordinates;

import java.util.ArrayList;

public class GameTable {
    private ArrayList<ArrayList<MinionCard>> table;
    private ArrayList<Coordinates> tankCoordinates;

    public GameTable() {
        table = new ArrayList<>(4);
        for (int i = 0; i < 4; ++i) {
            ArrayList<MinionCard> row = new ArrayList<>();
            table.add(row);
        }
        tankCoordinates = new ArrayList<Coordinates>();
    }

    public ArrayList<Coordinates> getTankCoordinates() {
        return tankCoordinates;
    }

    public void setTankCoordinates(ArrayList<Coordinates> tankCoordinates) {
        this.tankCoordinates = tankCoordinates;
    }

    public void placeCard(int row, MinionCard card) {
        if (row >= 0 && row < 4) {
            table.get(row).add(card);
        }
        if (card.getName().equals("Goliath") || card.getName().equals("Warden")) {
            Coordinates coordinates = new Coordinates(row, table.get(row).size() - 1);
            tankCoordinates.add(coordinates);
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
}
