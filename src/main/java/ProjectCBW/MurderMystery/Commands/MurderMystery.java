package ProjectCBW.MurderMystery.Commands;

import ProjectCBW.MurderMystery.DataStorage.Game.Game;
import ProjectCBW.MurderMystery.Functions.Essentials.Text;
import ProjectCBW.MurderMystery.Main;
import ProjectCBW.MurderMystery.StructuredQuery.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MurderMystery implements CommandExecutor {

    Game Game;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        Game = Main.getGame();
        switch (args[0].toUpperCase()) {
            case "CREATE":
                commandCreate(commandSender, command, label, args);
                return true;
            case "SET":
                commandSet(commandSender, command, label, args);
                return true;
            case "JOIN":
                if (args.length < 2) {
                    Game.addPlayer((Player) commandSender);
                    return true;
                }
                if ("**".equalsIgnoreCase(args[1])) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Game.addPlayer(player);
                    }
                    return true;
                }
                Player player = Bukkit.getOnlinePlayers().stream().filter(p -> p.getName().equalsIgnoreCase(args[1])).findFirst().get();
                Game.addPlayer(player);
                return true;
            case "LIST":
                for (Player listPlayer : Game.getJoinedPlayers()) {
                    commandSender.sendMessage(listPlayer.getName());
                }
                return true;
            case "PREPARE":
                prepareGame();
                return true;
            case "START":
                startGame();
                return true;
            case "STOP":
                stopGame();
                return true;
        }
        return false;
    }

    private void commandCreate(CommandSender commandSender, Command command, String label, String[] args) {
        switch (args[1].toUpperCase()) {
            case "WORLD":
                if (args.length < 3) {
                    commandSender.sendMessage(Text.getColoredText("&cError: Please select any world which you want to create data."));
                    return;
                }
                DatabaseManager.createWorld(args[2]);
                break;
        }
    }

    private void commandSet(CommandSender commandSender, Command command, String label, String[] args) {
        switch (args[1].toUpperCase()) {
            case "LOCATION":
                if (args.length < 4) {
                    commandSender.sendMessage(Text.getColoredText("&cError: Please type world name and location index number."));
                    return;
                }
                DatabaseManager.addLocation(args[2], Integer.parseInt(args[3]), ((Player) commandSender).getLocation());
        }
    }

    private void prepareGame() {
        Game.prepareGame();
    }

    private void startGame() {
        Game.startGame();
    }

    private void stopGame() {
        Game.endGame((short) 3);
    }


}
