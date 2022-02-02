package ProjectCBW.MurderMystery.Functions.GraphicalUserInterface.InventoryFramework;

import ProjectCBW.MurderMystery.DataStorage.Game.Game;
import ProjectCBW.MurderMystery.Functions.Essentials.Text;
import ProjectCBW.MurderMystery.Main;
import org.bukkit.entity.Player;

public class ItemClickEvent {

    public static void getJoinedPlayers(Player player) {
        Game Game = Main.getGame();
        String joinedPlayers = "";
        for (Player joinedPlayer : Game.getJoinedPlayers()) {
            String color = "&a";
            if (joinedPlayer == player) color = "&f";
            joinedPlayers = joinedPlayers.concat(String.format("%s%s&7, ", color, joinedPlayer));
        }
        joinedPlayers = joinedPlayers.substring(0, joinedPlayers.length() - 2);
        player.sendMessage(Text.getColoredText(joinedPlayers));
    }

}
