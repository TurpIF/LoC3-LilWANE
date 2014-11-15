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

    public Force(float currentForce, float aggressiveRatio, float defensiveRatio, float simpleRatio) {
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

    public static Force createAggressiveForce(Force force, int minimalForce, float aggressiveRatio, float defensiveRatio, float simpleRatio) {
        float totalRatios = aggressiveRatio + defensiveRatio + simpleRatio;
        aggressiveRatio /= totalRatios;
        defensiveRatio /= totalRatios;
        simpleRatio /= totalRatios;

        float currentForce = 0;
        while (currentForce < minimalForce) {
            currentForce += 2 * aggressiveRatio + defensiveRatio + simpleRatio;
        }

        return new Force(currentForce, aggressiveRatio, defensiveRatio, simpleRatio);
    }

    public static Force createDefensiveForce(Force force, int minimalForce, float aggressiveRatio, float defensiveRatio, float simpleRatio) {
        float totalRatios = aggressiveRatio + defensiveRatio + simpleRatio;
        aggressiveRatio /= totalRatios;
        defensiveRatio /= totalRatios;
        simpleRatio /= totalRatios;

        float currentForce = 0;
        while (currentForce < minimalForce) {
            currentForce += aggressiveRatio + 2* defensiveRatio + simpleRatio;
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
}
