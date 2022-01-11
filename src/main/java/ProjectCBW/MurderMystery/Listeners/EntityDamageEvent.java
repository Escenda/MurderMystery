package ProjectCBW.MurderMystery.Listeners;

import ProjectCBW.MurderMystery.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageEvent implements Listener {

    ProjectCBW.MurderMystery.DataStorage.Game.Game Game;

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        Game = Main.getGame();
        e.setCancelled(true);
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) return;
        if (Game.getState() != 2) return;
        Player Attacker = (Player) e.getDamager();
        Player Victim = (Player) e.getEntity();
        boolean AttackerIsMurder = Game.getMurders().contains(Attacker);
        if (!AttackerIsMurder) return;
        Game.killEvent(Attacker, Victim);
    }
}
