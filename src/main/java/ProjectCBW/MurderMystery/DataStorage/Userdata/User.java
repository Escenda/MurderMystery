package ProjectCBW.MurderMystery.DataStorage.Userdata;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class User {

    private final UUID UUID;
    private final String NAME;

    private final Data DATA; // Dataの変更は要らないかな？

    public User(UUID UUID) {
        Player player = Bukkit.getOfflinePlayer(UUID).getPlayer();
        this.UUID = UUID;
        this.NAME = player.getName();
        this.DATA = new Data(player); // インスタンス再生成回避の為にplayerインスタンスを渡してます
    }

    public User(Player player) {
        this.UUID = player.getUniqueId();
        this.NAME = player.getName();
        this.DATA = new Data(player);
    }

    public Data getData() {
        return DATA;
    }

}
