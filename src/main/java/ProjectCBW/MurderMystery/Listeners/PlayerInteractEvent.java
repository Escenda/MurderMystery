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
        Game = Main.getGame();
        Player eventPlayer = e.getPlayer();
        distanceAttack(e, eventPlayer);
    }

    private void distanceAttack(org.bukkit.event.player.PlayerInteractEvent e, Player player) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_AIR) && !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        Data data = Main.getPlayer(player).getData();
        if (data.getInterval() > 0) return;
        int interval = 5;
        data.setInterval(interval);
        for (int i = 1; i <= interval; i++) {
            int finalI = i;
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), () -> {
                if (finalI < interval) player.setExp(player.getExp() - (1f / interval)); else player.setExp(1f);
                data.setInterval(interval - finalI);
            }, 20L * i);
        }
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_LAUNCH, 10 ,1);
        new DistanceDamage(player);
    }

}
