package ProjectCBW.MurderMystery.Functions.Game;

import ProjectCBW.MurderMystery.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.concurrent.atomic.AtomicReference;

public class DistanceDamage {

    private final Player Attacker;

    private final Location bulletLocation;
    private final Vector bulletVector;

    private final ProjectCBW.MurderMystery.DataStorage.Game.Game Game;

    private int timeleft;
    private int scheduleId;

    public DistanceDamage(Player Attacker) {
        this.Attacker = Attacker;
        this.Game = Main.getGame();
        int interval = 2;
        timeleft = 3 * (20 / interval);
        scheduleId = 0;

        bulletLocation = Attacker.getEyeLocation();
        bulletVector = Attacker.getEyeLocation().getDirection();

        scheduleId = Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Main.class), () -> {
            timeleft--;
            if ((timeleft < 1 || Game.getState() != 2 || Main.getGame() != Game)
            || (bulletLocation.getBlock().getType() != Material.AIR)) {
                Bukkit.getScheduler().cancelTask(scheduleId);
                return;
            }

            bulletLocation.add(bulletVector);

            new ParticleBuilder(ParticleEffect.END_ROD, bulletLocation)
                    .display(Game.getJoinedPlayers());
            for (Player target : Game.getJoinedPlayers()) {
                if (target == Attacker || Game.getDiedPlayers().contains(target)) continue;
                Location targetLocation = target.getLocation();
                double xT = targetLocation.getX();
                double yT = targetLocation.getY();
                double zT = targetLocation.getZ();
                double xB = bulletLocation.getX();
                double yB = bulletLocation.getY();
                double zB = bulletLocation.getZ();
                double x = Math.max(xT, xB) - Math.min(xT, xB);
                double y = Math.max(yT, yB) - Math.min(yT, yB);
                double z = Math.max(zT, zB) - Math.min(zT, zB);
                double xDistance = Math.abs(x);
                double yDistance = Math.abs(y);
                double zDistance = Math.abs(z);
                if (xDistance > 0.5 || yDistance > 2 || zDistance > 0.5) continue;
                Game.killEvent(Attacker, target);
                Bukkit.getScheduler().cancelTask(scheduleId);
                return;
            }
        }, interval, interval);
    }
}
