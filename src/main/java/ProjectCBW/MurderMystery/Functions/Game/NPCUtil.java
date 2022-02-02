package ProjectCBW.MurderMystery.Functions.Game;

import ProjectCBW.MurderMystery.DataStorage.Game.Game;
import ProjectCBW.MurderMystery.Main;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.UUID;

public class NPCUtil {

    public static EntityPlayer spawnNPC(Player player) {
        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer craftWorld = ((CraftWorld) player.getWorld()).getHandle();
        EntityPlayer craftPlayer = ((CraftPlayer) player).getHandle();

        // テクスチャを取得する
        Property textures = (Property) craftPlayer.getProfile().getProperties().get("textures").toArray()[0];
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), player.getName());
        gameProfile.getProperties().put("textures", new Property("textures", textures.getValue(), textures.getSignature()));

        // 死体を作成する
        EntityPlayer corpse = new EntityPlayer(minecraftServer, craftWorld, gameProfile, new PlayerInteractManager(craftWorld));
        corpse.setPosition(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());

        // 見た目をリアルにする為に、偽のベッドを生成する
        Location bed = player.getLocation().add(1, 0, 0);
        corpse.e(new BlockPosition(bed.getX(), bed.getY(), bed.getZ()));

        // ネームタグを隠す
        ScoreboardTeam team = new ScoreboardTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(), player.getName());
        team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
        ArrayList<String> playerToAdd = new ArrayList<>();
        playerToAdd.add(corpse.getName());

        // 死体のポーズを変更、スキンの2層目を表示
        PacketPlayOutBed laying = new PacketPlayOutBed(corpse, new BlockPosition(bed.getX(), bed.getY(), bed.getZ()));
        DataWatcher watcher = craftPlayer.getDataWatcher();
        PacketPlayOutEntity.PacketPlayOutRelEntityMove move = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(corpse.getId(), (byte) 0, (byte) ((player.getLocation().getY() - 1.3 - player.getLocation().getY()) * 32), (byte) 0, false);

        // パケットを送信する
        Game Game = Main.getGame();
        for (Player joinedPlayer : Game.getJoinedPlayers()) {
            PlayerConnection connection = ((CraftPlayer) joinedPlayer).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, corpse));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(corpse));
            connection.sendPacket(new PacketPlayOutScoreboardTeam(team, 1));
            connection.sendPacket(new PacketPlayOutScoreboardTeam(team, 0));
            connection.sendPacket(new PacketPlayOutScoreboardTeam(team, playerToAdd, 3));
            connection.sendPacket(laying);
            connection.sendPacket(new PacketPlayOutEntityMetadata(corpse.getId(), watcher, true));
            connection.sendPacket(move);

            new BukkitRunnable() {
                public void run() {
                    connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY, corpse));
                }
            }.runTaskAsynchronously(Main.getPlugin(Main.class));
        }

        return corpse;

    }

}
