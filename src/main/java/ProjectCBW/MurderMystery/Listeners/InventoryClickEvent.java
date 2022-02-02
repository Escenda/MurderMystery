package ProjectCBW.MurderMystery.Listeners;

import ProjectCBW.MurderMystery.DataStorage.Config;
import ProjectCBW.MurderMystery.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class InventoryClickEvent implements Listener {

    @EventHandler
    public void onInventoryClick(org.bukkit.event.inventory.InventoryClickEvent e) {
        Config inventoryConfig = Main.getInventories();
        InventoryView inventory = e.getView();

        if (inventory.getTitle() == null) return;
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getItemMeta() == null) return;
        if (e.getCurrentItem().getItemMeta().getDisplayName() == null) return;
        e.setCancelled(true);

        if (Objects.equals(inventory.getTitle(), inventoryConfig.get("inventories.Menu.title"))) { onMenuClicked(e); }
        else if (Objects.equals(inventory.getTitle(), inventoryConfig.get("inventories.else.title"))) {
            // do something
        }
    }

    private void onMenuClicked(org.bukkit.event.inventory.InventoryClickEvent e) {
        Config itemConfig = Main.getItems();
        Player player = (Player) e.getWhoClicked();

        if (Objects.equals(e.getCurrentItem().getType().name(), Material.STAINED_GLASS_PANE)
        || (Objects.equals(e.getCurrentItem().getType().name(), Material.STAINED_GLASS_PANE))) { return; }

        if (Objects.equals(e.getSlot(), itemConfig.get("items.else.slot"))) {
            // do something
        }
    }

}
