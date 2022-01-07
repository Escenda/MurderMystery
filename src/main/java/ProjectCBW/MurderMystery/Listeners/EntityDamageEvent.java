package ProjectCBW.MurderMystery.Listeners;

import ProjectCBW.MurderMystery.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageEvent implements Listener {

    ProjectCBW.MurderMystery.DataStorage.Game Game = Main.getGame();

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        e.setCancelled(true);
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) return;
        Player Attacker = (Player) e.getDamager();
        Player Victim = (Player) e.getEntity();
        Attacker.sendMessage("You attacked to " + Victim.getName());
        Victim.sendMessage("You damaged from " + Attacker.getName());
        boolean AttackerIsMurder = Game.getMurders().contains(Attacker);
        if (!AttackerIsMurder) return;
        Game.killEvent(Attacker, Victim);
    }
}
