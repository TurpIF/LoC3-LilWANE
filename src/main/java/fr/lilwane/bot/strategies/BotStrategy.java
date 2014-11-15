package fr.lilwane.bot.strategies;

import com.d2si.loc.api.datas.Board;
import com.d2si.loc.api.datas.Troop;

import java.util.List;

/**
 * Bot strategy used to make choices at each key step of each turn.
 */
public interface BotStrategy {
    /**
     * Strategy interface run at each turn so that the bot can decide what troops to create.
     *
     * @param board
     * @return the troops to create for this turn
     */
    public List<Troop> createNewTroops(Board board);
}
