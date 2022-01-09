package ProjectCBW.MurderMystery;

import ProjectCBW.MurderMystery.Commands.MurderMystery;
import ProjectCBW.MurderMystery.DataStorage.Game;
import ProjectCBW.MurderMystery.DataStorage.Userdata.User;
import ProjectCBW.MurderMystery.Functions.Essentials.Text;
import ProjectCBW.MurderMystery.Functions.GraphicalUserInterface.Scoreboard;
import ProjectCBW.MurderMystery.Functions.GraphicalUserInterface.TabList;
import ProjectCBW.MurderMystery.Listeners.*;
import ProjectCBW.MurderMystery.StructuredQuery.MySQL;
import com.keenant.tabbed.Tabbed;
import com.keenant.tabbed.tablist.TableTabList;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public final class Main extends JavaPlugin {

    private static final MySQL SQL = new MySQL();

    private static final String[] headerTexts = Text.createAnimatedText("Murder Mystery", ChatColor.RED, ChatColor.WHITE, ChatColor.DARK_RED);

    private static Game Game = new Game();

    private static Map<Player, User> players = new HashMap<>();
    private static Map<Player, BPlayerBoard> boards = new HashMap<>();

    private FileConfiguration items;

    private void createFile() {
        File file = new File(getDataFolder(), "items.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            saveResource("items.yml", false);
        }

        items = new YamlConfiguration();

        try {
            items.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

    // SQLを返します。
    public static MySQL getSQL() {
        return SQL;
    }

    public FileConfiguration getItems() {
        return items;
    }

    // ゲームのインスタンスを返します。
    public static Game getGame() { return Game; }

    public static void setGame(Game Game) { Main.Game = Game; }

    public static User getPlayer(Player player) { //
        return players.get(player);
    }

    public static void addPlayer(Player player, User user) {
        players.put(player, user);
    }

    public static void removePlayer(Player player) {
        players.remove(player);
    }

    public static BPlayerBoard getBoard(Player player) { //
        return boards.get(player);
    }

    public static void addBoard(Player player, BPlayerBoard board) { boards.put(player, board); }

    public static void removeBoard(Player player) {
        boards.remove(player);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        new Tabbed(this);

        try {
            Main.SQL.connect();
        } catch (SQLException ignored) { ignored.printStackTrace(); }
        if (Main.SQL.isConnected()) {
            Bukkit.getLogger().info(" \u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u304c\u6b63\u5e38\u306b\u63a5\u7d9a\u3055\u308c\u307e\u3057\u305f\u3002");
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            addPlayer(player, new User(player));
            addBoard(player, Scoreboard.createBoard(player));
            TabList.createTab(player);
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                if (!Bukkit.getOnlinePlayers().contains(player)) return;
                getPlayer(player).getData().addExperience(1);
                Scoreboard.updateBoard(player);
                TabList.updateTab(player);
            }, 20, 20);
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                if (!Bukkit.getOnlinePlayers().contains(player)) return;
                for (int index = 0; index < headerTexts.length; index++) {
                    final int i = index;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                        if (!Bukkit.getOnlinePlayers().contains(player)) return;
                        getBoard(player).setName(headerTexts[i]);
                        TabList.updateHeader(player, headerTexts[i]);
                    }, 5L * i);
                }
            }, 0, (5L * headerTexts.length) + 100);
        }

        this.getServer().getPluginManager().registerEvents(new EntityDamageEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerInteractEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerMoveEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerQuitEvent(), this);
        this.getCommand("MurderMystery").setExecutor(new MurderMystery());
        this.getCommand("MurderMystery").setTabCompleter(new ProjectCBW.MurderMystery.Commands.TabCompletion.MurderMystery());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (Player player : Bukkit.getOnlinePlayers()) {
            Main.getPlayer(player).getData().saveData();
            Main.removePlayer(player);
            Main.getBoard(player).delete();
            Main.removeBoard(player);
            Tabbed.getTabbed(JavaPlugin.getPlugin(Main.class)).destroyTabList(player);
        }
    }

}
