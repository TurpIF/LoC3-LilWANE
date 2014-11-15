package fr.lilwane.models;

import com.d2si.loc.api.datas.Castle;
import com.d2si.loc.api.datas.Troop;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Force {
    // Main logger
    private static final Logger log = LogManager.getLogger(Force.class);

    private int aggressiveUnitCount;
    private int defensiveUnitCount;
    private int simpleUnitCount;

    public Force(int aggressiveUnitCount, int defensiveUnitCount, int simpleUnitCount) {
        this.aggressiveUnitCount = aggressiveUnitCount;
        this.defensiveUnitCount = defensiveUnitCount;
        this.simpleUnitCount = simpleUnitCount;
    }

    public Force(double currentForce, double aggressiveRatio, double defensiveRatio, double simpleRatio) {
        aggressiveUnitCount = (int) (currentForce * aggressiveRatio);
        defensiveUnitCount = (int) (currentForce * defensiveRatio);
        simpleUnitCount = (int) (currentForce * simpleRatio);
    }

    public Force(Castle castle) {
        this(castle.getAggressiveUnitCount(),
                castle.getDefensiveUnitCount(),
                castle.getSimpleUnitCount());
    }

    public Force(Troop troop) {
        this(troop.getAggressiveUnitCount(),
                troop.getDeffensiveUnitCount(),
                troop.getSimpleUnitCount());
    }

    public Force(Force f) {
        this(f.aggressiveUnitCount,
                f.defensiveUnitCount,
                f.simpleUnitCount);
    }

    public static Force createAggressiveForce(Force force, int minimalForce, double aggressiveRatio, double defensiveRatio, double simpleRatio) {
        double totalRatios = aggressiveRatio + defensiveRatio + simpleRatio;
        aggressiveRatio /= totalRatios;
        defensiveRatio /= totalRatios;
        simpleRatio /= totalRatios;

        double currentForce = 0;
        int nbAgg = 0;
        int nbDef = 0;
        int nbSim = 0;
        while (currentForce < minimalForce) {
            double c = currentForce;
            if (nbAgg < force.getAggressiveUnitCount()) {
                currentForce += aggressiveRatio * 2;
                nbAgg++;
            }
            if (nbDef < force.getDefensiveUnitCount()) {
                currentForce += defensiveRatio;
                nbDef++;
            }
            if (nbSim < force.getSimpleUnitCount()) {
                currentForce += simpleRatio;
                nbSim++;
            }
            if (c == currentForce) {
                break;
            }
        }

        return new Force(nbAgg, nbDef, nbSim);
    }

    public static Force createDefensiveForce(Force force, int minimalForce, double aggressiveRatio, double defensiveRatio, double simpleRatio) {
        double totalRatios = aggressiveRatio + defensiveRatio + simpleRatio;
        aggressiveRatio /= totalRatios;
        defensiveRatio /= totalRatios;
        simpleRatio /= totalRatios;

        double currentForce = 0;
        int nbAgg = 0;
        int nbDef = 0;
        int nbSim = 0;
        while (currentForce < minimalForce) {
            double c = currentForce;

            if (nbAgg < force.getAggressiveUnitCount()) {
                currentForce += aggressiveRatio;
                nbAgg++;
            }
            if (nbDef < force.getDefensiveUnitCount()) {
                currentForce += defensiveRatio * 2;
                nbDef++;
            }
            if (nbSim < force.getSimpleUnitCount()) {
                currentForce += simpleRatio;
                nbSim++;
            }
            if (c == currentForce) {
                break;
            }
        }

        return new Force(nbAgg, nbDef, nbSim);
    }

    public int getAggressiveUnitCount() {
        return aggressiveUnitCount;
    }

    public void setAggressiveUnitCount(int aggressiveUnitCount) {
        this.aggressiveUnitCount = aggressiveUnitCount;
    }

    public int getDefensiveUnitCount() {
        return defensiveUnitCount;
    }

    public void setDefensiveUnitCount(int defensiveUnitCount) {
        this.defensiveUnitCount = defensiveUnitCount;
    }

    public int getSimpleUnitCount() {
        return simpleUnitCount;
    }

    public void setSimpleUnitCount(int simpleUnitCount) {
        this.simpleUnitCount = simpleUnitCount;
    }

    public int getTotalUnits() {
        return aggressiveUnitCount + defensiveUnitCount + simpleUnitCount;
    }

    public int getAggressiveForce() {
        return aggressiveUnitCount * 2 + defensiveUnitCount + simpleUnitCount;
    }

    public int getDefensiveForce() {
        return aggressiveUnitCount + defensiveUnitCount * 2 + simpleUnitCount;
    }

    /**
     * Remove a force from another, in place.
     *
     * @param force
     */
    public void remove(Force force) {
        aggressiveUnitCount -= force.aggressiveUnitCount;
        defensiveUnitCount -= force.defensiveUnitCount;
        simpleUnitCount -= force.simpleUnitCount;
    }

    /**
     * Remove a force from another, in place.
     *
     * @param amount
     * @param aggressiveRatio
     * @param defensiveRatio
     * @param simpleRatio
     */
    public void remove(double amount, double aggressiveRatio, double defensiveRatio, double simpleRatio) {
        remove(new Force((int) (amount * aggressiveRatio),
                (int) (amount * defensiveRatio),
                (int) (amount * simpleRatio)));
    }

    public Force minus(Force force) {
        Force newForce = new Force(this);
        newForce.remove(force);
        return newForce;
    }

    public Force divide(double amount) {
        Force f = new Force(this);
        f.aggressiveUnitCount /= amount;
        f.defensiveUnitCount /= amount;
        f.simpleUnitCount /= amount;
        return f;
    }
}
