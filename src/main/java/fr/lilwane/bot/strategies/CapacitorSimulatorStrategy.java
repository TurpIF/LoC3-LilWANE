package fr.lilwane.bot.strategies;

import com.d2si.loc.api.datas.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CapacitorSimulatorStrategy implements BotStrategy {
    public static final int EXPANSION_BUDGET = 3;
    public static final int NEW_CASTLE_GAIN = 100;
    public static final double UNIT_SPEED = 5.0;

    private Double timeToGo(Castle origin, Castle other) {
        Coordinate posOrigin = origin.getPosition();
        Coordinate posOther = other.getPosition();

        Double dist = Math.sqrt(Math.pow(posOrigin.getX() - posOther.getX(), 2)
                + Math.pow(posOrigin.getY() - posOther.getY(), 2));
        return dist / UNIT_SPEED;
    }

    private Integer troopsToSend(Castle origin, Castle other, Board board) {
        int  n = other.getUnitCount() + 1;

        // On prend en compte la croissance
        // n += other.getGrowthRate() * (int) Math.floor(timeToGo(origin, other));

        // On enlève nos troupes en déplacement
        n -= board.getMineTroops()
                .stream()
                .filter(t -> t.getDestination().equals(other))
                .mapToInt(Troop::getUnitCount)
                .sum();

        // On ajoute les troupes ennemies en déplacement
        n += board.getOpponentsTroops()
                .stream()
                .filter(t -> t.getDestination().equals(other))
                .mapToInt(Troop::getUnitCount)
                .sum();

        return n;
    }

    private Double costCastle(Castle origin, Castle other) {
        final Integer K = 5; // On investit sur un chateau uniquement pour 5 tours
        final Double TAU = K / 5.0; // Constante de décharge d'une exp (cf Condensateur)

        Double t = timeToGo(origin, other);

        // Limite max de prod
        int k = Math.min(K, other.getRemainingUnitToCreate() / other.getGrowthRate());

        Double gain = 0.0;
        Double loss = 0.0;

        if (!other.getOwner().equals(Owner.Mine)) {
            loss = (double) other.getUnitCount() + other.getGrowthRate() * t;
        }

        if (!other.getOwner().equals(Owner.Mine)) {
            // Formule sum(e^(i/tau), i in [0..k])
            gain = (Math.exp((k + 1.0) / TAU) - 1.0) / (Math.exp(1.0 / TAU) - 1.0) * other.getGrowthRate();
            gain += NEW_CASTLE_GAIN;
        }

        return (gain - loss) / (t + K);
    }

    @Override
    public List<Troop> createNewTroops(Board board) {
        // Decide what troops to send on this new turn
        List<Troop> troopsToSendOnThisTurn = new ArrayList<>();

        // Send units on each castles I own
        List<Castle> myCastles = board.getMineCastles();
        for (Castle castle : myCastles) {

            // Get all "enemy" castles (opponent or neutral)
            List<Castle> allCastles = board.getCastles()
                    .stream()
                    .filter(c -> !c.equals(castle) && (c.getOwner() != Owner.Mine))
                    .collect(Collectors.toList());

            Coordinate castlePos = castle.getPosition();
            allCastles.sort((a, b) -> {
                return costCastle(castle, b).compareTo(costCastle(castle, a));
            });

            int nbSoldiers = castle.getUnitCount() / EXPANSION_BUDGET;
            for (Castle enemyCastle : allCastles) {
                // Leave at least one soldier on the castle
                if (nbSoldiers <= 1) {
                    break;
                }

                // Send the minumum amount of units (not all nbSoldiers though)
                int nbUnitsToSend = Math.min(nbSoldiers - 1, troopsToSend(castle, enemyCastle, board));
                if (nbUnitsToSend <= 0) {
                    continue;
                }
                Troop troop = new Troop(enemyCastle, castle, nbUnitsToSend);
                nbSoldiers -= nbUnitsToSend;
                troopsToSendOnThisTurn.add(troop);
            }
        }

        return troopsToSendOnThisTurn;
    }
}
