package fr.lilwane.bot;

import java.util.List;

import com.d2si.loc.api.Bot;
import com.d2si.loc.api.datas.Board;
import com.d2si.loc.api.datas.Castle;
import com.d2si.loc.api.datas.Coordinate;
import com.d2si.loc.api.datas.Troop;

import fr.lilwane.bot.strategies.BotStrategy;
import fr.lilwane.bot.strategies.CapacitorSimulatorStrategy;
import fr.lilwane.bot.strategies.ClosestCastleStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main implements Bot {
    // Main logger
    private static final Logger log = LogManager.getLogger(Main.class);

    // Bot strategy
    // BotStrategy strategy = new ClosestCastleStrategy();
    BotStrategy strategy = new CapacitorSimulatorStrategy();


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
        return strategy.createNewTroops(board);
    }
}
