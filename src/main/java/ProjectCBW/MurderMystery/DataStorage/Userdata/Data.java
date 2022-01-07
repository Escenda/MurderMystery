package ProjectCBW.MurderMystery.DataStorage.Userdata;

import ProjectCBW.MurderMystery.StructuredQuery.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Data {

    private Player player;

    private UUID UUID;
    private String NAME;

    private final String[] primaryKey = new String[]{"UUID", ""};

    private short Rank; // 0 = Default, 1 = VIP, 2 = VIP+, 3 = Elite, 4 = Elite+. #DEMO

    private int level;
    private long experience;

    private String positionName;

    public Data(Player player) {
        this.player = player;
        this.UUID = player.getUniqueId();
        this.NAME = player.getName();
        primaryKey[1] = this.UUID.toString();
        loadData();
    }

    public Data(UUID UUID) {
        Player player = Bukkit.getOfflinePlayer(UUID).getPlayer();
        if (player.getUniqueId() != UUID) return; // ないとは思うけど念の為
        this.player = player;
        this.UUID = UUID;
        this.NAME = player.getName();
        primaryKey[1] = this.UUID.toString();
        loadData();
    }

    private void loadData() {
        this.Rank = (short) DatabaseManager.getData("Rank", "data", primaryKey);
        this.level = (short) DatabaseManager.getData("Level", "data", primaryKey);
        this.experience = (short) DatabaseManager.getData("Experience", "data", primaryKey);
    }

    public short getRank() {
        return Rank;
    }

    public void setRank(short newValue) {
        this.Rank = newValue;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(long newValue) {
        this.experience = newValue;
    }

    public void addLevel(long level) {
        this.experience += level;
    }

    public long getExperience() {
        return experience;
    }

    public void setExperience(long newValue) {
        this.experience = newValue;
    }

    public void addExperience(long experience) {
        this.experience += experience;
    }

    public String getPositionName() { return positionName; }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

}
