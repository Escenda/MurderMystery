package ProjectCBW.MurderMystery.Functions.GraphicalUserInterface;

import ProjectCBW.MurderMystery.DataStorage.Userdata.Data;
import ProjectCBW.MurderMystery.Functions.Essentials.Text;
import ProjectCBW.MurderMystery.Functions.PingUtil;
import ProjectCBW.MurderMystery.Main;
import com.keenant.tabbed.Tabbed;
import com.keenant.tabbed.item.TabItem;
import com.keenant.tabbed.item.TextTabItem;
import com.keenant.tabbed.tablist.TableTabList;
import com.keenant.tabbed.util.Skins;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TabList {

    private static final Tabbed tabbed = Tabbed.getTabbed(JavaPlugin.getPlugin(Main.class));

    private static final Map<Player, TableTabList> tabList = new HashMap<>();

    public static void createTab(Player player) {
        Data data = Main.getPlayer(player).getData();
        TableTabList tab = tabbed.newTableTabList(player, 4, 20);

        tab.setHeaderFooter(Text.getColoredText("&5Murder Mystery"), Text.getColoredText("&9Join us on Discord -> https://discord.gg/UPk6RpqmRA"));
        tab.set(0, new TextTabItem(Text.getColoredText("&f&lProfile"), 1000, Skins.getDot(ChatColor.WHITE)));
        tab.set(1, new TextTabItem(player.getDisplayName(), (int) PingUtil.getPing(player), Skins.getPlayer(player)));

        for (int i = 0; i < tab.getMaxItems() / 20; i++) {
            switch (i * 20) {
                case 20:
                    tab.set(i * 20, new TextTabItem(Text.getColoredText("&b&lLevel"), 1000, Skins.getDot(ChatColor.AQUA)));
                    tab.set(i * 20 + 1, new TextTabItem(Text.getColoredText(String.format("&7%s Lv", String.format("%,d", data.getLevel()))), 1000, Skins.getDot(ChatColor.AQUA)));
                    break;
                case 40:
                    tab.set(i * 20, new TextTabItem(Text.getColoredText("&a&lExperience"), 1000, Skins.getDot(ChatColor.GREEN)));
                    tab.set(i * 20 + 1, new TextTabItem(Text.getColoredText(String.format("&7%s Exp", String.format("%,d", data.getExperience()))), 1000, Skins.getDot(ChatColor.GREEN)));
                    break;
                case 60:
                    tab.set(i * 20, new TextTabItem(Text.getColoredText("&7&lPosition"), 1000, Skins.getDot(ChatColor.GRAY)));
                    tab.set(i * 20 + 1, new TextTabItem(Text.getColoredText(String.format("&7%s", data.getPositionName())), 1000, Skins.getDot(ChatColor.GRAY)));
                    break;
            }
        }

        tab.set(new TableTabList.TableCell(0, 3), new TextTabItem(Text.getColoredText("&a   \u25c6            \u25c6"), 1000));
        tab.set(new TableTabList.TableCell(1, 3), new TextTabItem(Text.getColoredText("&a     \u25c7 ONLINE \u25c7"), 1000));
        tab.set(new TableTabList.TableCell(2, 3), new TextTabItem(Text.getColoredText("&a   \u25c6            \u25c6"), 1000));
        tab.set(new TableTabList.TableCell(3, 3), new TextTabItem(Text.getColoredText("&3     \u25cb HELPER \u25cb"), 1000));

        int Column = 0;
        int Row = 4;
        int OperatorRow = 4;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.isOp()) {
                tab.set(new TableTabList.TableCell(3, OperatorRow), new TextTabItem(onlinePlayer.getDisplayName(), (int) PingUtil.getPing(onlinePlayer), Skins.getPlayer(onlinePlayer)));
                OperatorRow++;
            } else {
                if (Column == 3) { Row++; Column = 0; }
                if (onlinePlayer == player) continue;
                tab.set(new TableTabList.TableCell(Column, Row), new TextTabItem(onlinePlayer.getDisplayName(), (int) PingUtil.getPing(onlinePlayer), Skins.getPlayer(onlinePlayer)));
                Column++;
            }
        }

        tabList.put(player, tab);

    }

    public static void updateTab(Player player) {
        BlankTab(player);
        TableTabList tab = tabList.get(player);
        Data data = Main.getPlayer(player).getData();
        tab.set(21, new TextTabItem(Text.getColoredText(String.format("&7%s Lv", String.format("%,d", data.getLevel()))), 1000, Skins.getDot(ChatColor.AQUA)));
        tab.set(41, new TextTabItem(Text.getColoredText(String.format("&7%s Exp", String.format("%,d", data.getExperience()))), 1000, Skins.getDot(ChatColor.GREEN)));
        tab.set(61, new TextTabItem(Text.getColoredText(String.format("&7%s", data.getPositionName())), 1000, Skins.getDot(ChatColor.GRAY)));
        int Column = 0;
        int Row = 4;
        int OperatorRow = 4;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.isOp()) {
                tab.set(new TableTabList.TableCell(3, OperatorRow), new TextTabItem(onlinePlayer.getDisplayName(), (int) PingUtil.getPing(onlinePlayer), Skins.getPlayer(onlinePlayer)));
                OperatorRow++;
            } else {
                if (Column == 3) { Row++; Column = 0; }
                if (onlinePlayer == player) continue;
                tab.set(new TableTabList.TableCell(Column, Row), new TextTabItem(onlinePlayer.getDisplayName(), (int) PingUtil.getPing(onlinePlayer), Skins.getPlayer(onlinePlayer)));
                Column++;
            }
        }
    }

    public static void BlankTab(Player player) {
        TableTabList tab = tabList.get(player);
        Map<TableTabList.TableCell, String> playerList = new HashMap<>();
        int spaceBetween = 0;
        int Column = 0;
        int Row = 4;
        for (int i = 0; i < 64; i++, Column++) {
            if (Column == 4) { Column = 0; Row++; }
            TableTabList.TableCell Cell = new TableTabList.TableCell(Column, Row);
            TabItem item = tab.get(Cell);
            if (item == null) { if (spaceBetween++ > 3) break; continue; }
            String Name = item.getText();
            if (playerList.containsValue(Name)) { tab.remove(Cell); continue; }
            List<String> Names = Bukkit.getOnlinePlayers().stream().map(Player::getDisplayName).collect(Collectors.toList());
            if (!Names.contains(Name)) { tab.remove(Cell); continue; }
            playerList.put(Cell, Name);
        }
    }

    public static void updateHeader(Player player, String text) {
        TableTabList tab = tabList.get(player);
        tab.setHeader(Text.getColoredText(text));
    }

    public static void updateFooter(Player player, String text) {
        TableTabList tab = tabList.get(player);
        tab.setFooter(Text.getColoredText(text));
    }

}
