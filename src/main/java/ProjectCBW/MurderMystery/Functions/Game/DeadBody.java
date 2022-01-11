package ProjectCBW.MurderMystery.Functions.Game;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.PlayerInteractManager;
import net.minecraft.server.v1_12_R1.WorldServer;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Collection;
import java.util.UUID;

public class DeadBody implements Listener {

    public final static int SERVER_VIEW_DISTANCE = Bukkit.getServer().getViewDistance();
    public final static byte PLAYER_SLEEP_HEIGHT_FIX = 0xF / 0xA;
    private final Plugin plugin;

    public DeadBody(Plugin plugin) {
        this.plugin = plugin;
    }

    public static EntityPlayer createPlayer(Player player) {
        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer(); // get server
        WorldServer world = ((CraftWorld) player.getWorld()).getHandle(); // get the world the player who died was standing in.
        GameProfile botGameProfile = new GameProfile(UUID.randomUUID(), player.getName()); // create a gameProfile for the bot
        GameProfile playerProfile = ((CraftPlayer) player).getProfile(); // get the skin of the player who just died
        Collection<Property> skinProperty = playerProfile.getProperties().get("textures"); // get his textures
        botGameProfile.getProperties().putAll("textures", skinProperty); // put the textures on the bot
        PlayerInteractManager interactManager = new PlayerInteractManager(world);
        EntityPlayer botPlayer = new EntityPlayer(minecraftServer, world, botGameProfile, interactManager); // initialize bot

        Location playerLocation = player.getLocation();
        botPlayer.setLocation(playerLocation.getX(), // set his location on player death
                playerLocation.getY(),
                playerLocation.getZ(),
                playerLocation.getYaw(),
                playerLocation.getPitch());
        return botPlayer;
    }
}
