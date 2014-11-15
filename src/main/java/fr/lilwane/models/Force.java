package fr.lilwane.models;

import com.d2si.loc.api.datas.Castle;
import com.d2si.loc.api.datas.Troop;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Create a distributed force taking a source force and a minimal force wanted among this one (provided that it
     * can create this one). Distribute the aggressive, defensive and neutral forces given the distribution triplet,
     * and multiply each component accordingly to the weight triplet.
     *
     * @param force the source force to take from
     * @param minimalForce the minimum desired force
     * @param distributions
     * @param weights
     * @return
     */
    private static Force createDistributedForce(Force force, int minimalForce, List<Double> distributions, List<Double> weights) {
        double totalDistributions = distributions.stream().mapToDouble((x) -> x).sum();
        distributions = distributions.stream().map((x) -> x / totalDistributions).collect(Collectors.toList());

        double currentForce = 0;
        int nbAgg = 0;
        int nbDef = 0;
        int nbSim = 0;
        while (currentForce < minimalForce) {
            double c = currentForce;

            // Add aggressive units if available
            if (nbAgg < force.getAggressiveUnitCount()) {
                currentForce += distributions.get(0) * weights.get(0);
                nbAgg++;
            }

            // Add defensive units if available
            if (nbDef < force.getDefensiveUnitCount()) {
                currentForce += distributions.get(1) * weights.get(1);
                nbDef++;
            }

            // Add simple units if available
            if (nbSim < force.getSimpleUnitCount()) {
                currentForce += distributions.get(2) * weights.get(2);
                nbSim++;
            }

            // Break if no changes
            if (c == currentForce) {
                break;
            }
        }

        return new Force(nbAgg, nbDef, nbSim);
    }

    /**
     * Create a distributed force taking a source force and a minimal force wanted among this one (provided that it
     * can create this one). Distribute the aggressive, defensive and neutral forces given the distribution triplet,
     * and multiply each component accordingly to the weight triplet.
     *
     * @param force the source force to take from
     * @param minimalForce the minimum desired force
     * @param distributions
     * @param weights
     * @return
     */
    private static Force createDistributedForce(Force force, int minimalForce, double[] distributions, double[] weights) {
        // Create distributions/weights triplet
        List<Double> distributionLs = new ArrayList<Double>();
        List<Double> weightLs = new ArrayList<Double>();
        for (int i = 0; i < 3; i++) {
            distributionLs.add(distributions[i]);
            weightLs.add(weights[i]);
        }

        return createDistributedForce(force, minimalForce, distributionLs, weightLs);
    }

    enum Mode {
        DEFENSIVE,
        OFFENSIVE,
        BALANCED
    }

    private static Force createDistributedForce(Force force, int minimalForce, double aggressiveRatio, double defensiveRatio, double simpleRatio, Mode mode) {
        double [] weights = null;
        switch (mode) {
            case OFFENSIVE:
                weights = new double[]{2., 1., 1.};

            case DEFENSIVE:
                weights = new double[]{1., 2., 1.};

            case BALANCED:
                weights = new double[]{1., 1., 1.};
        }
        double [] distributions = {aggressiveRatio, defensiveRatio, simpleRatio};
        return createDistributedForce(force, minimalForce, distributions, weights);
    }

    public static Force createAggressiveForce(Force force, int minimalForce, double aggressiveRatio, double defensiveRatio, double simpleRatio) {
        return createDistributedForce(force, minimalForce, aggressiveRatio, defensiveRatio, simpleRatio, Mode.OFFENSIVE);
    }

    public static Force createDefensiveForce(Force force, int minimalForce, double aggressiveRatio, double defensiveRatio, double simpleRatio) {
        return createDistributedForce(force, minimalForce, aggressiveRatio, defensiveRatio, simpleRatio, Mode.OFFENSIVE);
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
