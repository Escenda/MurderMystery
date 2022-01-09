package ProjectCBW.MurderMystery.Listeners;

import ProjectCBW.MurderMystery.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.ArrayList;
import java.util.List;

public class PlayerMoveEvent implements Listener {

    ProjectCBW.MurderMystery.DataStorage.Game Game = Main.getGame();

    @EventHandler
    public void onPlayerMove(org.bukkit.event.player.PlayerMoveEvent e) {
        if (Game.getState() != 2) return;
        Player eventPlayer = e.getPlayer();
        if (!Game.getInnocents().contains(eventPlayer)) return;
        List<Player> seeablePlayers = new ArrayList<>();
        seeablePlayers.addAll(Game.getDiedPlayers());
        seeablePlayers.addAll(Game.getMurders());
        new ParticleBuilder(ParticleEffect.FOOTSTEP, eventPlayer.getLocation())
                .display(seeablePlayers);
    }
}
