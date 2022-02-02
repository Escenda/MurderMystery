package ProjectCBW.MurderMystery.Listeners;

import ProjectCBW.MurderMystery.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.ArrayList;
import java.util.List;

public class PlayerMoveEvent implements Listener {

    ProjectCBW.MurderMystery.DataStorage.Game.Game Game;

    private static final List<Player> list = new ArrayList<>();

    @EventHandler
    public void onPlayerMove(org.bukkit.event.player.PlayerMoveEvent e) {
        Game = Main.getGame();
        if (Game.getState() != 2) return;
        Player eventPlayer = e.getPlayer();
        if (Game.getMurders().contains(eventPlayer)) return;
        if (Game.getDiedPlayers().contains(eventPlayer)) return;
        if (list.contains(eventPlayer)) return;
        List<Player> seeablePlayers = new ArrayList<>();
        seeablePlayers.add(eventPlayer);
        seeablePlayers.addAll(Game.getDiedPlayers());
        seeablePlayers.addAll(Game.getMurders());
        Location location = eventPlayer.getLocation();
        location.add(0, 0.1, 0);
        for (int i = 0; i < 3; i++) {
            new ParticleBuilder(ParticleEffect.FOOTSTEP, location)
                    .display(seeablePlayers);
        }
        list.add(eventPlayer);
        Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Main.class), () -> list.remove(eventPlayer), 10);
    }
}
