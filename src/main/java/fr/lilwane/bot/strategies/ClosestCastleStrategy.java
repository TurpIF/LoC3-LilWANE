package fr.lilwane.bot.strategies;

import com.d2si.loc.api.datas.Board;
import com.d2si.loc.api.datas.Castle;
import com.d2si.loc.api.datas.Coordinate;
import com.d2si.loc.api.datas.Troop;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple strategy using the "closest is best" method.
 */
public class ClosestCastleStrategy implements BotStrategy {
    @Override
    public List<Troop> createNewTroops(Board board) {
        // Decide what troops to send on this new turn
        List<Troop> troopsToSendOnThisTurn = new ArrayList<>();

        // Send units on each castles I own
        List<Castle> myCastles = board.getMineCastles();
        for (Castle castle : myCastles) {

            // Get all "enemy" castles (opponent or neutral)
            List<Castle> enemyCastles = board.getOpponentsCastles();
            enemyCastles.addAll(board.getNeutralCastles());

            Coordinate castlePos = castle.getPosition();
            enemyCastles.sort((a, b) -> {
                Coordinate posA = a.getPosition();
                Coordinate posB = b.getPosition();

                Double distA = Math.sqrt(Math.pow(posA.getX() - castlePos.getX(), 2) + Math.pow(posA.getY() - castlePos.getY(), 2));
                Double distB = Math.sqrt(Math.pow(castlePos.getX() - posB.getX(), 2) + Math.pow(castlePos.getY() - posB.getY(), 2));

                return distA.compareTo(distB);
            });

            int nbSoldiers = castle.getUnitCount();
            for (Castle enemyCastle : enemyCastles) {
                // Leave at least one soldier on the castle
                if (nbSoldiers <= 1) {
                    break;
                }

                // Send the minumum amount of units (not all nbSoldiers though)
                int nbUnitsToSend = Math.min(nbSoldiers - 1, enemyCastle.getUnitCount() + 1);
                Troop troop = new Troop(enemyCastle, castle, nbUnitsToSend);
                nbSoldiers -= nbUnitsToSend;
                troopsToSendOnThisTurn.add(troop);
            }
        }

        return troopsToSendOnThisTurn;
    }
}
