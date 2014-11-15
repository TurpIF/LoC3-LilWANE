package fr.lilwane.bot;

import java.util.List;

import com.d2si.loc.api.Bot;
import com.d2si.loc.api.datas.Board;
import com.d2si.loc.api.datas.Castle;
import com.d2si.loc.api.datas.Coordinate;
import com.d2si.loc.api.datas.Troop;

import fr.lilwane.bot.strategies.BotStrategy;
import fr.lilwane.bot.strategies.ClosestCastleStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main implements Bot {
    // Main logger
    private static final Logger log = LogManager.getLogger(Main.class);

    // Bot strategy
    BotStrategy strategy = new ClosestCastleStrategy();

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

    public Double costCastle(Castle origin, Castle other) {
        final Integer K = 5; // On investit sur un chateau uniquement pour 5 tours
        final Double TAU = K / 5.0; // Constante de décharge d'une exp (cf Condensateur)

        Coordinate posOrigin = origin.getPosition();
        Coordinate posOther = other.getPosition();

        Double dist = Math.sqrt(Math.pow(posOrigin.getX() - posOther.getX(), 2)
                + Math.pow(posOrigin.getY() - posOther.getY(), 2));
        Double t = dist / 5.0; // magic number

        // Formule sum(e^(i/tau), i in [0..k])
        Double gain = (Math.exp((K + 1.0) / TAU) - 1.0) / (Math.exp(1.0 / TAU) - 1.0);

        return gain / (t + K);
    }

    @Override
    public List<Troop> onNewTurn(Board board) {
        return strategy.createNewTroops(board);
    }
}
