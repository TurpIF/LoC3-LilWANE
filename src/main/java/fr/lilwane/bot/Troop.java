package main.java.fr.lilwane.bot;

public class Troop extends Regiment {
    private Castle source;
    private Castle destination;

    public Troop(int unitCount, Position position, Team team, Castle source, Castle destination) {
        super(unitCount, position, team);
        this.source = source;
        this.destination = destination;
    }

    public Castle getSource() {
        return source;
    }

    public void setSource(Castle source) {
        this.source = source;
    }

    public Castle getDestination() {
        return destination;
    }

    public void setDestination(Castle destination) {
        this.destination = destination;
    }
}
