package main.java.fr.lilwane.bot;

public class Castle extends Regiment {
    /**
     * Number of units available for troop creation in this castle.
     */
    private int availableUnitCount;

    public Castle(int unitCount, Position position, Team team, int availableUnitCount) {
        super(unitCount, position, team);
        this.availableUnitCount = availableUnitCount;
    }

    public int getAvailableUnitCount() {
        return availableUnitCount;
    }

    public void setAvailableUnitCount(int availableUnitCount) {
        this.availableUnitCount = availableUnitCount;
    }
}
