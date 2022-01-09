package ProjectCBW.MurderMystery.Commands.TabCompletion;

import ProjectCBW.MurderMystery.StructuredQuery.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MurderMystery implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        List<String> list = new ArrayList<>();
        switch (args.length) {
            case 1:
                list = Arrays.asList("create", "set", "join", "list", "prepare", "start", "stop");
                break;
            case 2:
                switch (args[0].toUpperCase()) {
                    case "JOIN":
                        list = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
                        list.add("**");
                        break;
                    case "CREATE":
                        list = Arrays.asList("world", "");
                        break;
                    case "SET":
                        list = Arrays.asList("location", "");
                        break;
                }
            case 3:
                switch (args[0].toUpperCase()) {
                    case "CREATE":
                        switch (args[1].toUpperCase()) {
                            case "WORLD":
                                list = Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
                                break;
                        }
                    case "SET":
                        switch (args[1].toUpperCase()) {
                            case "LOCATION":
                                list = DatabaseManager.getAll("name", "worlds");
                                for (String str : list) {
                                    commandSender.sendMessage(str);
                                }
                                break;
                        }
                }
        }
        return Objects.requireNonNull(list).stream().filter(value -> value.toUpperCase().contains(args[args.length - 1].toUpperCase())).collect(Collectors.toList());
    }
}
