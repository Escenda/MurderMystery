package ProjectCBW.MurderMystery.DataStorage;

import ProjectCBW.MurderMystery.Functions.Essentials.Text;
import ProjectCBW.MurderMystery.Main;
import ProjectCBW.MurderMystery.StructuredQuery.DatabaseManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {

    private short state; // 0 = STOPPED, 1 = PREPARED, 2 = STARTED.
    private short endCode; // short endCode : 0 == Murder killed any other players, 1 == Player survived from murders caused by timeout, 2 == Player survived from murders caused by all the murders was destroyed, 3 == Game ended caused by Operator command sent.
    private final short GameMode; // short GameMode : 0 = Normal, 1 = Mega.

    private Object gameTask;

    private String selectedWorldName;

    private final List<Integer> selectedIndexes;

    private List<Player> joinedPlayers; // GameMode Normal = 12 of Max, Mega = 24 of Max.
    private final List<Player> Murders;
    private final List<Player> Innocents;
    private final List<Player> diedMurders;
    private final List<Player> diedInnocents;

    private int timeleft;

    public Game() {
        state = 0;
        GameMode = 0;
        selectedIndexes = new ArrayList<>();
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
        selectedIndexes = new ArrayList<>();
        joinedPlayers = new ArrayList<>();
        Murders = new ArrayList<>();
        Innocents = new ArrayList<>();
        diedMurders = new ArrayList<>();
        diedInnocents = new ArrayList<>();
    }

    // type: 0 == spectatorLocation, 1 == RandomizedLocation.
    private Location getLocation(short type) {
        String UUIDString = DatabaseManager.get("locations", "worlds", new String[]{"name", selectedWorldName}).toString();
        int index = -1;
        switch (type) {
            case 0:
                index = 0;
                break;
            case 1:
                int countLocations = DatabaseManager.countTable("locations", new String[]{"uuid", UUIDString});
                Random random = new Random();
                index = random.nextInt(countLocations - 1);
                while (selectedIndexes.contains(index) && joinedPlayers.size() > (countLocations - 1))
                    index = random.nextInt(countLocations - 1); // spectatorLocation などの余分な引数がカウントされているので、その分を手動で値を決めて除去
                selectedIndexes.add(index);
                break;
        }
        World world = Bukkit.getWorld(selectedWorldName);
        List<String[]> primaryKeys = new ArrayList<>();
        primaryKeys.add(new String[]{"uuid", UUIDString});
        primaryKeys.add(new String[]{"index", String.valueOf(index)});
        double x = (double) DatabaseManager.get("x", "locations", primaryKeys);
        double y = (double) DatabaseManager.get("y", "locations", primaryKeys);
        double z = (double) DatabaseManager.get("z", "locations", primaryKeys);
        float yaw = (float) DatabaseManager.get("yaw", "locations", primaryKeys);
        float pitch = (float) DatabaseManager.get("pitch", "locations", primaryKeys);
        return new Location(world, x, y, z, yaw, pitch);
    }

    // ゲームインスタンスの進捗状況を取得して返す
    public short getState() {
        return state;
    }

    // 終了コードを取得して返す
    public short getEndCode() {
        return endCode;
    }

    // 参加プレイヤーリストを返す
    public List<Player> getJoinedPlayers() {
        return joinedPlayers;
    }

    // 殺人鬼のリストを返す
    public List<Player> getMurders() {
        return Murders;
    }

    // 市民のリストを返す
    public List<Player> getInnocents() {
        return Innocents;
    }

    public List<Player> getDiedPlayers() {
        List<Player> diedPlayers = new ArrayList<>();
        diedPlayers.addAll(diedInnocents);
        diedPlayers.addAll(diedMurders);
        return diedPlayers;
    }

    // 参加プレイヤーリストにプレイヤーを追加
    public void addPlayer(Player player) {
        if (joinedPlayers.contains(player)) return;
        joinedPlayers.add(player);
        Bukkit.broadcastMessage(Text.getColoredText(String.format("&a%sさんがゲームに参加しました。", player.getDisplayName())));
    }

    // すべてリセット
    // ゲーム終了時の処理に使える //
    public void resetAll() {
        World world = Bukkit.getWorld("world");
        double x = 0;
        double y = 75;
        double z = 0;
        float yaw = 0;
        float pitch = 0;
        Location location = new Location(world, x, y, z, yaw, pitch);
        Bukkit.broadcastMessage(Text.getColoredText("&cゲームをリセットしています。\n&cプレイヤーは自動でデモワールドへテレポートされます。"));
        for (Player player : joinedPlayers) {
            player.teleport(location);
            player.sendMessage(Text.getColoredText("&7あなたはデモワールドへテレポートされました。"));
        }
    }

    // 参加中のプレイヤーをリセット
    public boolean resetJoinedPlayers() {
        joinedPlayers = new ArrayList<>();
        return true;
    }

    // ゲーム開始前の処理 #TODO
    public void prepareGame() {
        if (joinedPlayers.size() < 3) {
            Bukkit.broadcastMessage(Text.getColoredText("&c参加人数が3人未満であった為、ゲームの準備は開始されませんでした。"));
            return; // if joined players are less than 3, game will not start.
        }

        if (GameMode == 1 && joinedPlayers.size() < 9) {
            Bukkit.broadcastMessage(Text.getColoredText("&c参加人数が9人未満であった為、ゲームの準備は開始されませんでした。"));
            return; // if joined players are less than 9 and GameMode is Mega mode, game will not start.
        }

//        selectedWorldName = (String) DatabaseManager.getRandomValue("name", "worlds");
        selectedWorldName = DatabaseManager.getRandomValue("name", "worlds").toString();
        Bukkit.broadcastMessage("Selected Map is " + selectedWorldName);

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
            while (selectedPlayers.contains(selectedPlayer))
                selectedPlayer = (short) random.nextInt(joinedPlayers.size());
            selectedPlayers.add(selectedPlayer);
            Murder = joinedPlayers.get(selectedPlayer);
            Murder.sendMessage(Text.getColoredText("&7You are a Murder"));
            Main.getPlayer(Murder).getData().setPositionName("Murder");
            Murders.add(Murder);
        }

        // 残ったプレイヤーを全員市民として追加
        for (short i = 0; i < joinedPlayers.size(); i++) {
            if (selectedPlayers.contains(i)) continue;
            Player Innocent = joinedPlayers.get(i);
            Innocent.sendMessage(Text.getColoredText("&7You are a Innocent"));
            Main.getPlayer(Innocent).getData().setPositionName("Innocent");
            Innocents.add(Innocent);
        }

        state = 1;

    }

    // ゲームを開始する際の処理
    public void startGame() {
        if (state != 1) {
            Bukkit.broadcastMessage(Text.getColoredText("&cゲームの準備が完了していない為、ゲームを開始出来ませんでした。"));
            return;
        }

        for (Player player : joinedPlayers) {
            player.teleport(getLocation((short) 1));
            if (!Main.getPlayer(player).getData().getPositionName().equalsIgnoreCase("Murder")) continue;
            player.getInventory().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
        }

        timeleft = 500; // 500秒
        state = 2;

        gameTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
            if (timeleft == 0) {
                endGame((short) 1);
                return;
            }
            timeleft--;
        }, 0, 20);
    }

    // ゲームを終了する際の処理 #TODO
    public void endGame(short endCode) {
        state = 0;
        this.endCode = endCode;
        Bukkit.getScheduler().cancelTask((int) gameTask);
        // Message getter, using endCode to get Messages.
        for (Player player : joinedPlayers) {
            player.sendMessage(Text.getColoredText("&7Game is finished"));
        }
        resetAll(); // #TODO
        Main.setGame(new Game());
    }

    // プレイヤーがキルされた際の処理
    public void killEvent(Player Attacker, Player Victim) {
        Location location = getLocation((short) 0);
        Attacker.playSound(Attacker.getLocation(), Sound.ENTITY_PLAYER_DEATH, 10, 1);
        Victim.playSound(Victim.getLocation(), Sound.ENTITY_PLAYER_DEATH, 10, 1);
        Attacker.sendMessage(Text.getColoredText("&7You killed Innocent"));
        Victim.sendMessage(Text.getColoredText("&7You died caused by murder killed you"));
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
