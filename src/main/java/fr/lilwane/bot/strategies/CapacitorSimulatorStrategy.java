package fr.lilwane.bot.strategies;

import com.d2si.loc.api.datas.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategy using a capacitor model to simulate investing in castle.
 */
public class CapacitorSimulatorStrategy implements BotStrategy {

    /**
     * Fraction of the budget (troops newly created) allocated to expansion.
     * A higher value means more defensive, a lower means more offensive.
     */
    public static final int EXPANSION_BUDGET = 3;

    /**
     * Since the game's objective is mainly to have castles, tilt the bot in favor of conquering new castles.
     * TODO compute this value (given the respective castles' gain?)
     */
    public static final int NEW_CASTLE_GAIN = 100;

    /**
     * Speed of a unit.
     * TODO this should be app-global
     */
    public static final double UNIT_SPEED = 5.0;

    /**
     * Computes the time estimated to move a new troop from castle "origin" to castle "other".
     *
     * @param origin the origin castle
     * @param other the destination castle
     * @return the time cost to go from origin to other
     */
    private Double timeToGo(Castle origin, Castle other) {
        Coordinate posOrigin = origin.getPosition();
        Coordinate posOther = other.getPosition();

        Double dist = Math.sqrt(Math.pow(posOrigin.getX() - posOther.getX(), 2)
                + Math.pow(posOrigin.getY() - posOther.getY(), 2));
        return dist / UNIT_SPEED;
    }

    /**
     * Cost for investing in a castle (by going there). Follows a capacitor law, in order to decide when
     * the return on investment (ROI) is neglectable.
     *
     * @param origin the origin castle
     * @param other the destination castle
     * @return the cost to invest in a castle
     */
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
                int nbUnitsToSend = Math.min(nbSoldiers - 1,
                        enemyCastle.getUnitCount()
                        // + enemyCastle.getGrowthRate() * timeToGo(castle, enemyCastle).intValue()
                        + 1);
                Troop troop = new Troop(enemyCastle, castle, nbUnitsToSend);
                nbSoldiers -= nbUnitsToSend;
                troopsToSendOnThisTurn.add(troop);
            }
        }

        return troopsToSendOnThisTurn;
    }
}
