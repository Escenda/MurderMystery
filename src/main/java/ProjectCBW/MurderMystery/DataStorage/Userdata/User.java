package ProjectCBW.MurderMystery.DataStorage.Userdata;

import ProjectCBW.MurderMystery.Main;
import ProjectCBW.MurderMystery.StructuredQuery.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class User {

    private final Player player;

    private final UUID UUID;
    private final String NAME;

    private final String[] primaryKey = new String[]{"uuid", ""};

    private Data DATA;
    private int DATA_SAVE_SCHEDULE_ID;

    public User(Player player) {
        this.player = player;
        this.UUID = player.getUniqueId();
        this.NAME = player.getName();
        primaryKey[1] = UUID.toString();
        loadUser();
        DATA_SAVE_SCHEDULE_ID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
            if (!Bukkit.getOnlinePlayers().contains(player)) {
                Bukkit.getScheduler().cancelTask(DATA_SAVE_SCHEDULE_ID);
                return;
            }
            DATA.saveData();
        }, 20 * 60, 20 * 60);
    }

    public void createUser() {
        DatabaseManager.createUser(player);
    }

    public void loadUser() {
        if (!DatabaseManager.exists("users", primaryKey)) createUser();
        String DataUUIDString = DatabaseManager.get("data", "users", primaryKey).toString();
        UUID DataUUID = java.util.UUID.fromString(DataUUIDString);
        this.DATA = new Data(DataUUID);
    }

    public Data getData() {
        return DATA;
    }

}
