package fr.d2si.loc.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.d2si.loc.api.Bot;
import com.d2si.loc.api.datas.Board;
import com.d2si.loc.api.datas.Castle;
import com.d2si.loc.api.datas.Troop;

public class BotTeam implements Bot {

	private static final Logger log = LogManager.getLogger(BotTeam.class);

	private Random r = new Random();

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
			int maxUnitToSend = castle.getUnitCount() - 1; // Let at least one unit on this castle!
			if (maxUnitToSend <= 1) {
				// Nothing to send on this castle
				continue;
			}

			// Compute random unit to send
			int unitToSend = r.nextInt(maxUnitToSend) + 1;

			// Find a random destination castle
			List<Castle> enemyCastles = board.getOpponentsCastles();
			if (enemyCastles.isEmpty()) {
				continue;
			}

			int castleIndex = r.nextInt(enemyCastles.size());
			Castle destination = enemyCastles.get(castleIndex);

			// Finally create troop object
			Troop troop = new Troop(destination, castle, unitToSend);
			troopsToSendOnThisTurn.add(troop);
		}

		// Return all new troops
		return troopsToSendOnThisTurn;
	}

}
