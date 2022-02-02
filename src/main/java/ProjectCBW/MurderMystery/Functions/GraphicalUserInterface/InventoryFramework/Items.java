package ProjectCBW.MurderMystery.Functions.GraphicalUserInterface.InventoryFramework;

import ProjectCBW.MurderMystery.Functions.Essentials.Text;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Items {

    public static ItemStack getItem(String name) {

        ItemStack item = new ItemStack(Material.STONE);
        ItemMeta meta = item.getItemMeta();

        switch (name.toLowerCase()) {
            case "": // TEMPLATE
                item = new ItemStack(Material.SKULL_ITEM);
                meta = item.getItemMeta();
                break;
            case "getjoinedplayers":
                item = new ItemStack(Material.BOOK);
                meta = item.getItemMeta();
                meta.setDisplayName("参加者リスト");
                meta.setLore(Collections.singletonList(Text.getColoredText("&7右クリックで参加者のリストを取得")));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + name.toLowerCase());
        }

        item.setItemMeta(meta);
        return item;
    }

}
