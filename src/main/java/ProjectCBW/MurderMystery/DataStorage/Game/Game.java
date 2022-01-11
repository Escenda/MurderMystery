package ProjectCBW.MurderMystery.DataStorage.Game;

import ProjectCBW.MurderMystery.Functions.Game.DeadBody;
import ProjectCBW.MurderMystery.Functions.Essentials.Text;
import ProjectCBW.MurderMystery.Main;
import ProjectCBW.MurderMystery.StructuredQuery.DatabaseManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {

    private short state; // 0 = STOPPED, 1 = PREPARED, 2 = STARTED.
    private short endCode; // short endCode : 0 == Murder killed any other players, 1 == Player survived from murders caused by timeout, 2 == Player survived from murders caused by all the murders was destroyed, 3 == Game ended caused by Operator command sent.
    private final short GameMode; // short GameMode : 0 = Normal, 1 = Mega.

    private int gameTask;
    private int emeraldSpawnTask;

    private String selectedWorldName;

    private final List<Integer> selectedIndexes;

    private List<Player> joinedPlayers; // GameMode Normal = 12 of Max, Mega = 24 of Max.
    private final List<Player> Murders;
    private final List<Player> Detectors;
    private final List<Player> Innocents;
    private final List<Player> diedMurders;
    private final List<Player> diedDetectors;
    private final List<Player> diedInnocents;

    private final List<EntityPlayer> DeadBodies;

    private int timeleft;

    public Game() {
        state = 0;
        GameMode = 0;
        selectedIndexes = new ArrayList<>();
        joinedPlayers = new ArrayList<>();
        Murders = new ArrayList<>();
        Detectors = new ArrayList<>();
        Innocents = new ArrayList<>();
        diedMurders = new ArrayList<>();
        diedDetectors = new ArrayList<>();
        diedInnocents = new ArrayList<>();
        DeadBodies = new ArrayList<>();
    }

    // ゲームモードを指定してインスタンスを作成
    public Game(short GameMode) {
        state = 0;
        this.GameMode = GameMode;
        selectedIndexes = new ArrayList<>();
        joinedPlayers = new ArrayList<>();
        Murders = new ArrayList<>();
        Detectors = new ArrayList<>();
        Innocents = new ArrayList<>();
        diedMurders = new ArrayList<>();
        diedDetectors = new ArrayList<>();
        diedInnocents = new ArrayList<>();
        DeadBodies = new ArrayList<>();
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
                while (selectedIndexes.contains(index) && joinedPlayers.size() < (countLocations - 1))
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

    // 探偵のリストを返す
    public List<Player> getDetectors() {
        return Detectors;
    }

    // 市民のリストを返す
    public List<Player> getInnocents() {
        return Innocents;
    }

    public List<Player> getDiedPlayers() {
        List<Player> diedPlayers = new ArrayList<>();
        diedPlayers.addAll(diedDetectors);
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
        for (Player player : getJoinedPlayers()) {
            player.teleport(location);
            player.sendMessage(Text.getColoredText("&7あなたはデモワールドへテレポートされました。"));
            for (EntityPlayer deadBody : DeadBodies)
                ((CraftPlayer) player).getHandle().playerConnection
                        .sendPacket(new PacketPlayOutEntityDestroy(deadBody.getId())); // let's destroy the bot after some time
            player.sendMessage(Text.getColoredText("&7Game is finished"));
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

        short _Detector = (short) random.nextInt(joinedPlayers.size());
        selectedPlayers.add(_Detector);
        Player Detector = joinedPlayers.get(_Detector);
        Detector.sendMessage(Text.getColoredText("&7You are a Detector"));
        Main.getPlayer(Detector).getData().setPositionName("Detector");
        Detectors.add(Detector);

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
            player.setExp(1f);
            if (Main.getPlayer(player).getData().getPositionName().equalsIgnoreCase("Murder"))
                player.getInventory().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
            else if (Main.getPlayer(player).getData().getPositionName().equalsIgnoreCase("Detector"))
                player.getInventory().setItemInMainHand(new ItemStack(Material.IRON_HOE));
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

        emeraldSpawnTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
            Location randomLocation = getLocation((short) 1);
            randomLocation.getWorld().dropItemNaturally(randomLocation, new ItemStack(Material.EMERALD));
        }, 20 * 10, 20 * 10);

    }

    // ゲームを終了する際の処理 #TODO
    public void endGame(short endCode) {
        state = 0;
        this.endCode = endCode;
        Bukkit.getScheduler().cancelTask((int) gameTask);
        // Message getter, using endCode to get Messages.\
        for (int i = 1; i <= 10; i++) {
            int _timeleft = 10 - i;
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), () -> {
                for (Player joinedPlayer : joinedPlayers) {
                    joinedPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(String.format("%s seconds then go back to the lobby automatically", _timeleft)));
                }
            }, 20 * i);
            if (i != 10) continue;
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), () -> {
                resetAll(); // #TODO
                Main.setGame(new Game());
            }, 20 * 10);
        }
    }

    // プレイヤーがキルされた際の処理
    public void killEvent(Player Attacker, Player Victim) {
        Location location = getLocation((short) 0);
        Location deathLocation = Victim.getLocation();
        EntityPlayer deadBody = DeadBody.createPlayer(Victim);
        BlockPosition deathBlockPosition = new BlockPosition(deathLocation.getX(), deathLocation.getY(), deathLocation.getZ()); // you should modify the Y value and adapt it with the height of the ground if the player didn't die on ground, I'm not doing it here.
        DeadBodies.add(deadBody);
        for (Player p : getJoinedPlayers()) {
            PlayerConnection conn = ((CraftPlayer) p).getHandle().playerConnection;
            conn.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, deadBody));
            conn.sendPacket(new PacketPlayOutNamedEntitySpawn(deadBody)); // spawn the bot to nearby players.
            conn.sendPacket(new PacketPlayOutBed(deadBody, deathBlockPosition)); // make the bot 'sleep'
            conn.sendPacket(new PacketPlayOutEntity.PacketPlayOutRelEntityMove(deadBody.getId(), // fix the bot height
                    (byte) 0, // we do not need to change X or Z values.
                    DeadBody.PLAYER_SLEEP_HEIGHT_FIX, // decrease height by 0.15f
                    (byte) 0,
                    true));
        }
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
