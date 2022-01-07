package ProjectCBW.MurderMystery.DataStorage;

import ProjectCBW.MurderMystery.Main;
import ProjectCBW.MurderMystery.StructuredQuery.DatabaseManager;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {

    private short state; // 0 = STOPPED, 1 = PREPARING, 2 = RUNNING.
    private short endCode; // short endCode : 0 == Murder killed any other players, 1 == Player survived from murders caused by timeout, 2 == Player survived from murders caused by all the murders was destroyed, 3 == Game ended caused by Operator command sent.
    private final short GameMode; // short GameMode : 0 = Normal, 1 = Mega.

    private Object gameTask;

    private String selectedWorldName;

    private List<Player> joinedPlayers; // GameMode Normal = 12 of Max, Mega = 24 of Max.
    private final List<Player> Murders;
    private final List<Player> Innocents;
    private final List<Player> diedMurders;
    private final List<Player> diedInnocents;

    private int timeleft;

    public Game() {
        state = 0;
        GameMode = 0;
        joinedPlayers = new ArrayList<>();
        Murders = new ArrayList<>();
        Innocents = new ArrayList<>();
        diedMurders = new ArrayList<>();
        diedInnocents = new ArrayList<>();

    }

    // ゲームモードを指定してインスタンスを作成
    public Game(short GameMode) {
        state = 0;
        this.GameMode = GameMode;
        joinedPlayers = new ArrayList<>();
        Murders = new ArrayList<>();
        Innocents = new ArrayList<>();
        diedMurders = new ArrayList<>();
        diedInnocents = new ArrayList<>();
    }

    // type: 0 == spectatorLocation, 1 == RandomizedLocation.
    private Location getLocation(short type) {
        String UUIDString = (String) DatabaseManager.getData("locations", "worlds", new String[]{"name", selectedWorldName});
        String index = null;
        switch (type) {
            case 0:
                index = "spectatorLocation";
                break;
            case 1:
                int countLocations = (int) DatabaseManager.countTable("locations", new String[]{"uuid", UUIDString});
                Random random = new Random();
                index = String.valueOf(random.nextInt(countLocations - 1)); // spectatorLocation などの余分な引数を除去
                break;
        }
        World world = Bukkit.getWorld(selectedWorldName);
        List<String[]> primaryKeys = new ArrayList<>();
        primaryKeys.add(new String[]{"uuid", UUIDString});
        primaryKeys.add(new String[]{"index", index});
        double x = (double) DatabaseManager.getData("x", "locations", primaryKeys);
        double y = (double) DatabaseManager.getData("y", "locations", primaryKeys);
        double z = (double) DatabaseManager.getData("z", "locations", primaryKeys);
        float yaw = (float) DatabaseManager.getData("yaw", "locations", primaryKeys);
        float pitch = (float) DatabaseManager.getData("pitch", "locations", primaryKeys);
        return new Location(world, x, y, z, yaw, pitch);
    }

    // 終了コードを取得して返す
    public short getEndCode() { return endCode; }

    // すべてリセット
    // ゲーム終了時の処理に使える //
    public void resetAll() {
        World world = Bukkit.getWorld("DEMO");
        double x = 0;
        double y = 0;
        double z = 0;
        float yaw = 0;
        float pitch = 0;
        Location location = new Location(world, x, y, z, yaw, pitch);
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                "&cゲームをリセットしています。\n&cプレイヤーは自動でデモワールドへテレポートされます。"));
        for (Player player : joinedPlayers) {
            player.teleport(location);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&7あなたはデモワールドへテレポートされました。"));
        }
    }

    // 参加プレイヤーリストを返す
    public List<Player> getJoinedPlayers() {
        return joinedPlayers;
    }

    // 参加プレイヤーリストにプレイヤーを追加
    public boolean addPlayer(Player player) {
        if (joinedPlayers.contains(player)) return false;
        joinedPlayers.add(player);
        return true;
    }

    // 参加中のプレイヤーをリセット
    public boolean resetJoinedPlayers() {
        joinedPlayers = new ArrayList<>();
        return true;
    }

    // 殺人鬼のリストを返す
    public List<Player> getMurders() {
        return Murders;
    }

    // 市民のリストを返す
    public List<Player> getInnocents() {
        return Innocents;
    }

    // ゲーム開始前の処理 #TODO
    public void prepareGame() {
        if (joinedPlayers.size() < 3) return; // if joined players are less than 3, game will not start.
        if (GameMode == 1 && joinedPlayers.size() < 9) return; // if joined players are less than 9 and GameMode is Mega mode, game will not start.

        selectedWorldName = (String) DatabaseManager.getRandomValue("name", "worlds");

        Random random = new Random();
        Player Murder;
        List<Short> selectedPlayers = new ArrayList<>();
        short maximumMurder = (short) -1;

        switch (GameMode) {
            case 0:
                maximumMurder = (short) 1;
                break;
            case 1:
                maximumMurder = (short) 2;
                break;
        }

        // 殺人鬼の最大人数を元にループ処理にてプレイヤーを選抜して追加
        for (short i = 0; i < maximumMurder; i++) {
            short selectedPlayer = (short) random.nextInt(joinedPlayers.size());
            while (selectedPlayers.contains(selectedPlayer)) selectedPlayer = (short) random.nextInt(joinedPlayers.size());
            selectedPlayers.add(selectedPlayer);
            Murder = joinedPlayers.get(selectedPlayer);
            Murder.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&7You are a Murder"));
            Murders.add(Murder);
        }

        // 残ったプレイヤーを全員市民として追加
        for (short i = 0; i < joinedPlayers.size(); i++) {
            if (selectedPlayers.contains(i)) continue;
            Player Innocent = joinedPlayers.get(i);
            Innocent.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&7You are a Innocent"));
            Innocents.add(Innocent);
        }

    }

    // ゲームを開始する際の処理
    public void startGame() {
        gameTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), runGame(), 0, 1000);
    }

    // ゲーム処理の継続的実行用のメソッド
    public Runnable runGame() {
        if (timeleft == 0) { endGame((short) 1); return null; }
        if (state == 1) {
            for (Player player : joinedPlayers) {
                Location location = getLocation((short) 1);
                player.teleport(player);
            }
            timeleft = 500; // 500秒
            state = 2;
        }
        timeleft--;
        return null;
    }

    // ゲームを終了する際の処理 #TODO
    public void endGame(short endCode) {
        state = 0;
        this.endCode = endCode;
        gameTask = null;
        // Message getter, using endCode to get Messages.
        for (Player player : joinedPlayers) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&7Game is finished"));
        }
        resetAll(); // #TODO
    }

    // プレイヤーがキルされた際の処理
    public void killEvent(Player Attacker, Player Victim) {
        Location location = getLocation((short) 0);
        Attacker.playSound(Attacker.getLocation(), Sound.ENTITY_PLAYER_DEATH, 10, 1);
        Victim.playSound(Victim.getLocation(), Sound.ENTITY_PLAYER_DEATH, 10, 1);
        Attacker.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7You killed Innocent"));
        Victim.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7You died caused by murder killed you"));
        Victim.setGameMode(org.bukkit.GameMode.SPECTATOR);
        Victim.teleport(location);
        diedEvent(Victim);
    }

    // プレイヤーが死んだ際の処理
    public void diedEvent(Player diedPlayer) {
        if (Murders.contains(diedPlayer)) diedMurders.add(diedPlayer); else diedInnocents.add(diedPlayer);
        if (diedInnocents.size() + Murders.size() == joinedPlayers.size()) { endGame((short) 0); return; }
        if (diedMurders.size() == Murders.size()) { endGame((short) 2); }
    }

}
