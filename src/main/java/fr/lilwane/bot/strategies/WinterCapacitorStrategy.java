package fr.lilwane.bot.strategies;

import com.d2si.loc.api.datas.*;
import fr.lilwane.models.Force;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Strategy using a capacitor model to simulate investing in castle.
 * NOTE : WINTER IS COMING !!!
 */
public class WinterCapacitorStrategy implements BotStrategy {
    private static final Logger log = LogManager.getLogger(WinterCapacitorStrategy.class);

    /**
     * Fraction of the budget (troops newly created) allocated to expansion.
     * A higher value means more defensive, a lower means more offensive.
     */
    public static final double AGGRESSIVE_EXPANSION_BUDGET = 1.1;
    public static final double DEFENSIVE_EXPANSION_BUDGET = 3.0;
    public static final double SIMPLE_EXPANSION_BUDGET = 1.1;

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

    // Decide what troops to send on this new turn
    private List<Troop> troopsToSendOnThisTurn = new ArrayList<>();

    private Map<Troop, Castle> troopDestinations = new HashMap<>();

    private Map<Castle, Force> castleForces = new HashMap<>();

    private void init(Board board) {
        troopsToSendOnThisTurn.clear();
        troopDestinations.clear();
        castleForces.clear();

        for (Castle c : board.getCastles()) {
            Force force = new Force(c).divide(getExpansionBudget(c));
            castleForces.put(c, force);
            castleForces.get(c).remove(optimalDefensiveForce(board, c));
        }
    }

    private double getExpansionBudget(Castle c) {
        switch (c.getUnitType()) {
            case Agressive:
                return AGGRESSIVE_EXPANSION_BUDGET;
            case Defensive:
                return DEFENSIVE_EXPANSION_BUDGET;
            case Simple:
                return SIMPLE_EXPANSION_BUDGET;
        }
        return SIMPLE_EXPANSION_BUDGET;
    }

    private Force optimalDefensiveForce(Board board, Castle castle) {
        return Force.createDefensiveForce(castleForces.get(castle),
                board.getOpponentsTroops()
                .stream()
                .filter(t -> t.getDestination().equals(castle))
                .mapToInt(t -> new Force(t).getAggressiveForce())
                .sum(), 0.1, 0.1, 1.0);
    }

    private void sendTroops(Castle from, Castle to, int forceToSend) {
        Force force;
        if (to.getOwner().equals(Owner.Mine)) {
            force = Force.createDefensiveForce(new Force(from), forceToSend, 0.1, 0.1, 1.0);
        }
        else {
            force = Force.createAggressiveForce(new Force(from), forceToSend, 1.0, 0.1, 0.1);
        }
        castleForces.get(from).remove(force);
        Troop troop = new Troop(to, from,
                Math.max(0, force.getAggressiveUnitCount() - 1),
                Math.max(0, force.getDefensiveUnitCount() - 1),
                Math.max(0, force.getSimpleUnitCount() - 1));
        troopsToSendOnThisTurn.add(troop);
        troopDestinations.put(troop, to);
    }

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
     * Computes the time estimated to move a new troop from troop "troop" to castle "castle".
     *
     * @param troop the origin castle
     * @param castle the destination castle
     * @return the time cost to go from origin to other
     */
    private Double timeToGo(Troop troop, Castle castle) {
        Coordinate posOrigin = troop.getPosition();
        Coordinate posOther = castle.getPosition();

        Double dist = Math.sqrt(Math.pow(posOrigin.getX() - posOther.getX(), 2)
                + Math.pow(posOrigin.getY() - posOther.getY(), 2));
        return dist / UNIT_SPEED;
    }

    private Castle getTroopDestination(Troop troop) {
        if (troopDestinations.containsKey(troop)) {
            return troopDestinations.get(troop);
        }
        return troop.getDestination();
    }

    private Integer troopForceToSend(Castle origin, Castle other, Board board) {
        Force force = new Force(other);

        // On prend en compte la croissance
        int n = 0;
        if (!other.getOwner().equals(Owner.Neutral)) {
            n = other.getGrowthRate() * ((int) Math.ceil(timeToGo(origin, other)) + 1);
        }

        if (other.getUnitType().equals(UnitType.Simple)) {
            force.setSimpleUnitCount(force.getSimpleUnitCount() + n);
        }
        if (other.getUnitType().equals(UnitType.Agressive)) {
            force.setAggressiveUnitCount(force.getAggressiveUnitCount() + n);
        }
        if (other.getUnitType().equals(UnitType.Defensive)) {
            force.setDefensiveUnitCount(force.getDefensiveUnitCount() + n);
        }

        log.debug("castle : " + other.getName()
        + " nbA : " + force.getAggressiveUnitCount()
        + " nbD : " + force.getDefensiveUnitCount()
        + " nbS : " + force.getSimpleUnitCount()
        + " grow : " + (other.getGrowthRate() * ((int) Math.ceil(timeToGo(origin, other)))));

        int forceCount;
        if (other.getOwner().equals(Owner.Mine)) {
            forceCount = force.getDefensiveForce();
        }
        else {
            forceCount = force.getAggressiveForce();
        }

        // On enlève nos troupes en déplacement
        forceCount -= board.getMineTroops()
                .stream()
                .filter(t -> getTroopDestination(t).equals(other))
                .mapToInt(t -> {
                    if (other.getOwner().equals(Owner.Mine)) {
                        return new Force(t).getDefensiveForce();
                    }
                    return new Force(t).getAggressiveForce();
                })
                .sum();

        // On ajoute les troupes ennemies en déplacement
        forceCount += board.getOpponentsTroops()
                .stream()
                .filter(t -> t.getDestination().equals(other))
                .mapToInt(t -> {
                    if (!other.getOwner().equals(Owner.Mine)) {
                        return new Force(t).getDefensiveForce();
                    }
                    return new Force(t).getAggressiveForce();
                })
                .sum();

        return forceCount;
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
            loss = (double) new Force(other).getDefensiveForce()
                    + other.getGrowthRate() * t * (other.getUnitType().equals(UnitType.Defensive) ? 2 : 1);
        }

        if (!other.getOwner().equals(Owner.Mine)) {
            // Formule sum(e^(i/tau), i in [0..k])
            double gainUnit = (Math.exp((k + 1.0) / TAU) - 1.0) / (Math.exp(1.0 / TAU) - 1.0) * other.getGrowthRate();
            gain += gainUnit * (other.getUnitType().equals(UnitType.Simple) ? 1 : 2);
            gain += NEW_CASTLE_GAIN;
        }

        return (gain - loss) / (t + K);
    }

    @Override
    public List<Troop> createNewTroops(Board board) {
        init(board);

        // Send units on each castles I own
        List<Castle> myCastles = board.getMineCastles();
        for (Castle castle : myCastles) {
            // Get all "enemy" castles (opponent or neutral)
            List<Castle> allCastles = board.getCastles()
                    .stream()
                    .filter(c -> !c.equals(castle) && (c.getOwner() != Owner.Mine))
                    .collect(Collectors.toList());

            allCastles.sort((a, b) -> costCastle(castle, b).compareTo(costCastle(castle, a)));

            Force force = castleForces.get(castle);
            for (Castle enemyCastle : allCastles) {
                // Leave at least one soldier on the castle
                if (force.getTotalUnits() <= 1) {
                    break;
                }

                // Send the minimum amount of units (not all nbSoldiers though)
                int nbTroopForcesToSend = troopForceToSend(castle, enemyCastle, board);
                if (nbTroopForcesToSend <= 0) {
                    continue;
                }
                log.debug("castle : " + enemyCastle.getName() + " # nbTroupForcesToSend : "
                        + nbTroopForcesToSend + " force : " + castleForces.get(castle).getAggressiveForce());
                if (nbTroopForcesToSend >= force.getAggressiveForce()) {
                    continue;
                }
                sendTroops(castle, enemyCastle, nbTroopForcesToSend);
            }
        }

        // Attaque des Ewoks : réunir pour régner
        // Send units on each castles I own
        List<Castle> otherCastles = board.getNeutralCastles();
        otherCastles.addAll(board.getOpponentsCastles());
        otherCastles.sort((a, b) -> new Double(new Force(b).getDefensiveForce())
                .compareTo((double) new Force(a).getDefensiveForce()));
        for (Castle other : otherCastles) {
            List<Castle> castles = board.getMineCastles()
                    .stream()
                    .filter(c -> castleForces.get(c).getTotalUnits() > 0)
                    .sorted((a, b) -> timeToGo(other, b).compareTo(timeToGo(other, a)))
                    .collect(Collectors.toList());
            int forceDef = new Force(other).getDefensiveForce();
            int forceAtk = 0;
            Map<Castle, Integer> potentials = new HashMap<>();

            for (Castle mine : castles) {
                if (forceDef < forceAtk) {
                    break;
                }
                Integer f = Math.min(castleForces.get(mine).getAggressiveForce(), forceDef);
                forceAtk += f;
                potentials.put(mine, f);
            }

            // On envoie la sauce
            if (forceDef < forceAtk) {
                for (Map.Entry<Castle, Integer> entry : potentials.entrySet()) {
                    sendTroops(entry.getKey(), other, entry.getValue());
                }
                break;
            }
        }

        return troopsToSendOnThisTurn;
    }
}
