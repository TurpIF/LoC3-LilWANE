package fr.lilwane.bot;

import java.util.ArrayList;
import java.util.List;

import com.d2si.loc.api.Bot;
import com.d2si.loc.api.datas.Board;
import com.d2si.loc.api.datas.Castle;
import com.d2si.loc.api.datas.Coordinate;
import com.d2si.loc.api.datas.Troop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main implements Bot {
    private static final Logger log = LogManager.getLogger(Main.class);

    //private Random r = new Random();

    @Override
    public void init(Board board) {
        // Special initializations (if needed)
        log.info("init: " + board);
    }

    @Override
    public void onTroopError(int turnNumber, String messageError, Troop troopInError) {
        // A previously troop sent was illegal...
        log.info("onTroopError: turnNumber[" + turnNumber + "] msg[" + messageError + "] troop:" + troopInError);
    }

    @Override
    public void onGameEnded(boolean areYouWinner, Board board) {
        // Am I winner?
        log.info("onGameEnded: winner[" + areYouWinner + "]");
    }

    @Override
    public List<Troop> onNewTurn(Board board) {
        // Decide what troops to send on this new turn
        List<Troop> troopsToSendOnThisTurn = new ArrayList<>();

        // Send units on each castles I own
        List<Castle> myCastles = board.getMineCastles();
        for (Castle castle : myCastles) {
            /*int maxUnitToSend = castle.getUnitCount() - 1; // Let at least one unit on this castle!
            if (maxUnitToSend <= 1) {
                // Nothing to send on this castle
                continue;
            }

            // Compute random unit to send
            int unitToSend = r.nextInt(maxUnitToSend) + 1;*/

            // Find a random destination castle
            List<Castle> enemyCastles = board.getOpponentsCastles();
            /*if (enemyCastles.isEmpty()) {
                continue;
            }*/

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
                if (nbSoldiers <= 0) {
                    break;
                } else {
                    // Finally create troop object
                    int nbUnitsToSend = enemyCastle.getUnitCount() + 1;
                    Troop troop = new Troop(enemyCastle, castle, nbUnitsToSend);
                    nbSoldiers -= nbUnitsToSend;
                    troopsToSendOnThisTurn.add(troop);
                }
            }

            /*int castleIndex = r.nextInt(enemyCastles.size());
            Castle destination = enemyCastles.get(castleIndex);

            // Finally create troop object
            Troop troop = new Troop(destination, castle, unitToSend);
            troopsToSendOnThisTurn.add(troop);*/
        }

        // Return all new troops
        return troopsToSendOnThisTurn;
    }
}
