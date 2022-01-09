package ProjectCBW.MurderMystery.Listeners;

import ProjectCBW.MurderMystery.DataStorage.Game;
import ProjectCBW.MurderMystery.Main;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

public class PlayerInteractEvent implements Listener {

    Game Game = Main.getGame();

    @EventHandler
    public void onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent e) {
        if (Game.getState() != 2) return;
        Player eventPlayer = e.getPlayer();
        if (!Game.getMurders().contains(eventPlayer)) return;
        if (!eventPlayer.getInventory().getItemInMainHand().getType().equals(Material.IRON_SWORD)) return;
        if (!e.getAction().equals(Action.RIGHT_CLICK_AIR) && !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        eventPlayer.playSound(eventPlayer.getLocation(), Sound.ENTITY_FIREWORK_LAUNCH, 10 ,1);
        for (int i = 0; i < 15; i += 1) {
            new ParticleBuilder(ParticleEffect.END_ROD, eventPlayer.getLocation())
                    .setOffsetX(i)
                    .setOffsetY(1)
                    .setOffsetZ(i)
                    .setSpeed(1)
                    .display();
        }
    }

}
