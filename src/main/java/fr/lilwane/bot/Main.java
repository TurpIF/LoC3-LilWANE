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

            // Get all "enemy" castles (opponent or neutral)
            List<Castle> enemyCastles = board.getNeutralCastles();
            enemyCastles.addAll(board.getOpponentsCastles());

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

        // Return all new troops
        return troopsToSendOnThisTurn;
    }
}
