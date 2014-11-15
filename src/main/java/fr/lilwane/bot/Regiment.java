package fr.lilwane.bot;

public abstract class Regiment {
    /**
     * The number of units available for battle in this regiment
     */
    private int unitCount;

    /**
     * The position of this regiment
     */
    private Position position;

    /**
     * The team of this regiment
     */
    private Team team;

    protected Regiment(int unitCount, Position position, Team team) {
        this.unitCount = unitCount;
        this.position = position;
        this.team = team;
    }

    public int getUnitCount() {
        return unitCount;
    }

    public void setUnitCount(int unitCount) {
        this.unitCount = unitCount;
    }
}
