package ProjectCBW.MurderMystery.Listeners;

import ProjectCBW.MurderMystery.DataStorage.Game;
import ProjectCBW.MurderMystery.Main;
import com.keenant.tabbed.Tabbed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerQuitEvent implements Listener {

    private Game Game = Main.getGame();

    @EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Main.getPlayer(player).getData().saveData();
        Main.removePlayer(player);
        Main.getBoard(player).delete();
        Main.removeBoard(player);
        Tabbed.getTabbed(JavaPlugin.getPlugin(Main.class)).destroyTabList(player);
        if (!Game.getJoinedPlayers().contains(player)) return;
        Game.diedEvent(player);
    }
}
