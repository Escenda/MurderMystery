package ProjectCBW.MurderMystery.DataStorage.Userdata;

import ProjectCBW.MurderMystery.StructuredQuery.DatabaseManager;

import java.util.UUID;

public class Data {

    private final UUID UUID;

    private final String[] primaryKey = new String[]{"uuid", ""};

    private int Rank; // 0 = Default, 1 = VIP, 2 = VIP+, 3 = Elite, 4 = Elite+. #DEMO

    private int level;
    private long experience;

    private String positionName;
    private int interval;

    public Data(UUID UUID) {
        this.UUID = UUID;
        this.primaryKey[1] = UUID.toString();
        this.positionName = "GOD";
        this.interval = 0;
        loadData();
    }

    private void createData() {
        DatabaseManager.createData(UUID);
    }

    private void loadData() {
        if (!DatabaseManager.exists("data", primaryKey)) createData();
        this.Rank = (int) DatabaseManager.get("rank", "data", primaryKey);
        this.level = (int) DatabaseManager.get("level", "data", primaryKey);
        this.experience = (long) DatabaseManager.get("experience", "data", primaryKey);
    }

    public void saveData() {
        DatabaseManager.set("rank", "data", Rank, primaryKey);
        DatabaseManager.set("level", "data", level, primaryKey);
        DatabaseManager.set("experience", "data", experience, primaryKey);
    }

    public int getRank() {
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

    public int getInterval() { return interval; }

    public void setInterval(int newValue) { this.interval =  newValue; }

}
