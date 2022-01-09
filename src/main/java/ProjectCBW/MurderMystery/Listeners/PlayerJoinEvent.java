package ProjectCBW.MurderMystery.Listeners;

import ProjectCBW.MurderMystery.DataStorage.Userdata.User;
import ProjectCBW.MurderMystery.Functions.Essentials.Text;
import ProjectCBW.MurderMystery.Functions.GraphicalUserInterface.Scoreboard;
import ProjectCBW.MurderMystery.Functions.GraphicalUserInterface.TabList;
import ProjectCBW.MurderMystery.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerJoinEvent implements Listener {

    private static final String[] headerTexts = Text.createAnimatedText("Murder Mystery", ChatColor.RED, ChatColor.WHITE, ChatColor.DARK_RED);

    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent e) {
        Player eventPlayer = e.getPlayer();
        Main.addPlayer(eventPlayer, new User(eventPlayer));
        Main.addBoard(eventPlayer, Scoreboard.createBoard(eventPlayer));
        TabList.createTab(eventPlayer);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
            if (!Bukkit.getOnlinePlayers().contains(eventPlayer)) return;
            Main.getPlayer(eventPlayer).getData().addExperience(1);
            Scoreboard.updateBoard(eventPlayer);
            TabList.updateTab(eventPlayer);
        }, 0, 20);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
            if (!Bukkit.getOnlinePlayers().contains(eventPlayer)) return;
            for (int index = 0; index < headerTexts.length; index++) {
                final int i = index;
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                    if (!Bukkit.getOnlinePlayers().contains(eventPlayer)) return;
                    Main.getBoard(eventPlayer).setName(headerTexts[i]);
                    TabList.updateHeader(eventPlayer, headerTexts[i]);
                }, 5L * i);
            }
        }, 0, (5L * headerTexts.length) + 100);
    }
}
