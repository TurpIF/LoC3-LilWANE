package fr.lilwane.bot;

import com.d2si.loc.api.datas.Castle;
import com.d2si.loc.api.datas.Coordinate;
import com.d2si.loc.api.datas.Troop;

/**
 * Various app-level helpers (static methods).
 */
public class Helpers {

    /**
     * Speed of a unit.
     */
    public static final double UNIT_SPEED = 5.0;

    /**
     * Compute the euclidean distance between two points.
     *
     * @param posOrigin
     * @param posOther
     * @return the distance between posOrigin and posOther
     */
    public static Double euclideanDistance(Coordinate posOrigin, Coordinate posOther) {
        return Math.sqrt(Math.pow(posOrigin.getX() - posOther.getX(), 2)
                + Math.pow(posOrigin.getY() - posOther.getY(), 2));
    }

    public static Double unitTravelingTime(Castle castle, Castle other) {
        return Helpers.euclideanDistance(castle.getPosition(), other.getPosition()) / UNIT_SPEED;
    }

    public static Double unitTravelingTime(Troop troop, Castle other) {
        return Helpers.euclideanDistance(troop.getPosition(), other.getPosition()) / UNIT_SPEED;
    }
}
