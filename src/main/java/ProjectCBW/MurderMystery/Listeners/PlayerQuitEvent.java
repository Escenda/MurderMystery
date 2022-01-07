package ProjectCBW.MurderMystery.Listeners;

import ProjectCBW.MurderMystery.DataStorage.Game;
import ProjectCBW.MurderMystery.Main;
import ProjectCBW.MurderMystery.StructuredQuery.DatabaseManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerQuitEvent implements Listener {

    private Game Game = Main.getGame();

    @EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (!Game.getJoinedPlayers().contains(player)) return;
        Game.diedEvent(player);
    }
}
