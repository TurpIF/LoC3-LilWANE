package fr.lilwane.models;

import com.d2si.loc.api.datas.Castle;
import com.d2si.loc.api.datas.Troop;

public class Force {
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

    public static Force createAggressiveForce(Force force, int minimalForce, double aggressiveRatio, double defensiveRatio, double simpleRatio) {
        double totalRatios = aggressiveRatio + defensiveRatio + simpleRatio;
        aggressiveRatio /= totalRatios;
        defensiveRatio /= totalRatios;
        simpleRatio /= totalRatios;

        double currentForce = 0;
        while (currentForce < minimalForce) {
            currentForce += 2 * aggressiveRatio + defensiveRatio + simpleRatio;
        }

        return new Force(currentForce, aggressiveRatio, defensiveRatio, simpleRatio);
    }

    public static Force createDefensiveForce(Force force, int minimalForce, double aggressiveRatio, double defensiveRatio, double simpleRatio) {
        double totalRatios = aggressiveRatio + defensiveRatio + simpleRatio;
        aggressiveRatio /= totalRatios;
        defensiveRatio /= totalRatios;
        simpleRatio /= totalRatios;

        double currentForce = 0;
        while (currentForce < minimalForce) {
            currentForce += aggressiveRatio + 2 * defensiveRatio + simpleRatio;
        }

        return new Force(currentForce, aggressiveRatio, defensiveRatio, simpleRatio);
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

    public void remove(Force force) {
        aggressiveUnitCount -= force.aggressiveUnitCount;
        defensiveUnitCount -= force.defensiveUnitCount;
        simpleUnitCount -= force.simpleUnitCount;
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
}
