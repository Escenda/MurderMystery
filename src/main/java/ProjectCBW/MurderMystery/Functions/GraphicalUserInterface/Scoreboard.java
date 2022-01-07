package ProjectCBW.MurderMystery.Functions.GraphicalUserInterface;

import ProjectCBW.MurderMystery.DataStorage.Userdata.User;
import ProjectCBW.MurderMystery.Functions.Essentials.Text;
import ProjectCBW.MurderMystery.Main;
import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Scoreboard {

    private static final java.time.format.DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public static BPlayerBoard createBoard(Player player) {
        User playerData = Main.getPlayer(player);
        BPlayerBoard board = Netherboard.instance().createBoard(player, "");
        board.setAll(
                dtf.format(LocalDateTime.now()),
                "",
                Text.getColoredText(String.format("Name: &e%s", player.getDisplayName())),
                Text.getColoredText(String.format("Rank: %s", playerData.getData().getRank())),
                "",
                Text.getColoredText(String.format("Position: %s", playerData.getData().getPositionName())),
                "",
                Text.getColoredText(String.format("Level: %s", playerData.getData().getLevel())),
                Text.getColoredText(String.format("Experience: %s", playerData.getData().getExperience())),
                ""
                );
        return board;
    }

    public static void updateBoard(Player player) {
        User playerData = Main.getPlayer(player);
        BPlayerBoard board = Main.getBoard(player);
        board.set(dtf.format(LocalDateTime.now()), 10);
        board.set(Text.getColoredText(String.format("Name: &e%s", player.getDisplayName())), 8);
        board.set(Text.getColoredText(String.format("Rank: %s", playerData.getData().getRank())), 7);
        board.set(Text.getColoredText(String.format("Position: %s", playerData.getData().getPositionName())), 5);
        board.set(Text.getColoredText(String.format("Level: %s", playerData.getData().getLevel())), 3);
        board.set(Text.getColoredText(String.format("Experience: %s", playerData.getData().getExperience())), 2);
    }

}
