package ProjectCBW.MurderMystery.Functions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PingUtil {

    private static final String serverName = Bukkit.getServer().getClass().getPackage().getName();
    private static final String serverVersion = serverName.substring(serverName.lastIndexOf(".") + 1);
    private static Class<?> CPClass;

    public static double getPing(Player player) {
        try {
            PingUtil.CPClass = Class.forName("org.bukkit.craftbukkit." + PingUtil.serverVersion + ".entity.CraftPlayer");
            Object CraftPlayer = PingUtil.CPClass.cast(player);
            Method getHandle = CraftPlayer.getClass().getMethod("getHandle");
            Object EntityPlayer = getHandle.invoke(CraftPlayer);
            return ((net.minecraft.server.v1_12_R1.EntityPlayer) EntityPlayer).ping;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

}
