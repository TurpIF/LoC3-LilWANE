package fr.lilwane.bot.strategies;

import com.d2si.loc.api.datas.Board;
import com.d2si.loc.api.datas.Troop;

import java.util.List;

public interface BotStrategy {
    public List<Troop> createNewTroops(Board board);
}
