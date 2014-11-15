package fr.lilwane.bot;

import com.d2si.loc.api.Bot;
import com.d2si.loc.api.datas.Board;
import com.d2si.loc.api.datas.Troop;
import fr.lilwane.bot.strategies.BotStrategy;
import fr.lilwane.bot.strategies.WinterCapacitorStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class Main implements Bot {
    // Main logger
    private static final Logger log = LogManager.getLogger(Main.class);

    // Bot strategy
    BotStrategy strategy = new WinterCapacitorStrategy();


    @Override
    public void init(Board board) {
        // Special initializations (if needed)
        log.info("init", board);
    }

    @Override
    public void onTroopError(int turnNumber, String messageError, Troop troopInError) {
        // A previously troop sent was illegal...
        log.info("onTroopError" + "turnNumber[" + turnNumber + "] msg[" + messageError + "] troop:" + troopInError);
    }

    @Override
    public void onGameEnded(boolean areYouWinner, Board board) {
        // Am I winner?
        log.info("onGameEnded" + "winner[" + areYouWinner + "]");
    }

    @Override
    public List<Troop> onNewTurn(Board board) {
        List<Troop> newTroopsToCreate = strategy.createNewTroops(board);
        newTroopsToCreate.forEach((troop) -> log.info("onNewTurn.newTroop", "turnNumber[" + board.getTurnNumber() + "]"
                                                      + "troop: " + troop));
        return newTroopsToCreate;
    }
}
