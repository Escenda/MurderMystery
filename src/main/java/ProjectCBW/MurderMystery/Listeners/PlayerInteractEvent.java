package ProjectCBW.MurderMystery.Listeners;

import ProjectCBW.MurderMystery.DataStorage.Game.Game;
import ProjectCBW.MurderMystery.DataStorage.Userdata.Data;
import ProjectCBW.MurderMystery.Functions.Game.DistanceDamage;
import ProjectCBW.MurderMystery.Main;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerInteractEvent implements Listener {

    Game Game;

    @EventHandler
    public void onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_AIR) && !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        Game = Main.getGame();
        if (Game.getState() != 2) return;
        Player eventPlayer = e.getPlayer();
        Data data = Main.getPlayer(eventPlayer).getData();
        if (data.getInterval() > 0) return;
        int interval = 5;
        data.setInterval(interval);
        for (int i = 1; i <= interval; i++) {
            int finalI = i;
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), () -> {
                if (finalI < interval) eventPlayer.setExp(eventPlayer.getExp() - (1f / interval)); else eventPlayer.setExp(1f);
                data.setInterval(interval - finalI);
            }, 20L * i);
        }
        eventPlayer.playSound(eventPlayer.getLocation(), Sound.ENTITY_FIREWORK_LAUNCH, 10 ,1);
        new DistanceDamage(eventPlayer);
    }

}
