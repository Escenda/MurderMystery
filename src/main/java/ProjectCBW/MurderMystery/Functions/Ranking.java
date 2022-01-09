package ProjectCBW.MurderMystery.Functions;

import ProjectCBW.MurderMystery.Functions.Essentials.Text;
import ProjectCBW.MurderMystery.StructuredQuery.DatabaseManager;
import org.bukkit.entity.Player;

import java.util.Map;

public class Ranking {

    public static void getRanking(Player player, String columnName) {
        Map<Object, Object> Ranking = DatabaseManager.getRanking(columnName);
        for (Object key : Ranking.keySet()) {
            short index = (short) key;
            Object[] value = (Object[]) Ranking.get(index);
            Player lPlayer = (Player) value[0];
            if (lPlayer.getUniqueId() == player.getUniqueId()) {
                player.sendMessage(Text.getColoredText(String.format("&c[%s] %s: %s", index, player.getDisplayName(), String.format("%,d", (long) value[1]))));
                continue;
            }
            if (index <= 10) {
                lPlayer.sendMessage(Text.getColoredText(String.format("&7[%s] %s: %s", index, lPlayer.getDisplayName(), String.format("%,d", (long) value[1]))));
                continue;
            }
            player.sendMessage(Text.getColoredText(String.format("\n&c[%s] %s: %s", index, player.getDisplayName(), String.format("%,d", (long) value[1]))));
            return;
        }
    }
}
