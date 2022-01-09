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
                " ",
                Text.getColoredText(String.format("Name: &e%s", player.getDisplayName())),
                Text.getColoredText(String.format("Rank: %s", playerData.getData().getRank())),
                "  ",
                Text.getColoredText(String.format("Position: %s", playerData.getData().getPositionName())),
                "   ",
                Text.getColoredText(String.format("Level: %s", playerData.getData().getLevel())),
                Text.getColoredText(String.format("Experience: %s", playerData.getData().getExperience())),
                "    ",
                Text.getColoredText("&9Discord: UPk6RpqmRA")
        );
        return board;
    }

    public static void updateBoard(Player player) {
        User playerData = Main.getPlayer(player);
        BPlayerBoard board = Main.getBoard(player);
        board.set(dtf.format(LocalDateTime.now()), 11);
        board.set(Text.getColoredText(String.format("Name: &e%s", player.getDisplayName())), 9);
        board.set(Text.getColoredText(String.format("Rank: %s", playerData.getData().getRank())), 8);
        board.set(Text.getColoredText(String.format("Position: %s", playerData.getData().getPositionName())), 6);
        board.set(Text.getColoredText(String.format("Level: %s", playerData.getData().getLevel())), 4);
        board.set(Text.getColoredText(String.format("Experience: %s", playerData.getData().getExperience())), 3);
    }

}
