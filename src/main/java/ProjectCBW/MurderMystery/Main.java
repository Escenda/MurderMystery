package ProjectCBW.MurderMystery;

import ProjectCBW.MurderMystery.DataStorage.Game;
import ProjectCBW.MurderMystery.DataStorage.Userdata.User;
import ProjectCBW.MurderMystery.Listeners.EntityDamageEvent;
import ProjectCBW.MurderMystery.Listeners.PlayerJoinEvent;
import ProjectCBW.MurderMystery.Listeners.PlayerQuitEvent;
import ProjectCBW.MurderMystery.StructuredQuery.MySQL;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class Main extends JavaPlugin {

    private static final MySQL SQL = new MySQL();

    private FileConfiguration items;

    private static final Game Game = new Game();

    private static Map<Player, User> players = new HashMap<>();
    private static Map<Player, BPlayerBoard> boards = new HashMap<>();

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

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getServer().getPluginManager().registerEvents(new EntityDamageEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerQuitEvent(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public FileConfiguration getItems() {
        return items;
    }

    // SQLを返します。
    public static MySQL getSQL() { return SQL; }

    // ゲームのインスタンスを返します。
    public static ProjectCBW.MurderMystery.DataStorage.Game getGame() {
        return Game;
    }

    public static User getPlayer(Player player) { //
        return players.get(player);
    }

    public static BPlayerBoard getBoard(Player player) { //
        return boards.get(player);
    }

}
