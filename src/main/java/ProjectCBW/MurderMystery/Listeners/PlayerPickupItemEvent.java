package ProjectCBW.MurderMystery.Listeners;

import ProjectCBW.MurderMystery.DataStorage.Game.Game;
import ProjectCBW.MurderMystery.Main;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class PlayerPickupItemEvent implements Listener {

    Game Game;

    @EventHandler
    public void onItemPickup(org.bukkit.event.player.PlayerPickupItemEvent e) {
        Game = Main.getGame();
        if (Game.getState() != 2) return;
        if (e.getItem().getItemStack().getType() != Material.EMERALD) return;
        if (!Game.getInnocents().contains(e.getPlayer())) return;
        int emeraldCount = 0;
        for (ItemStack itemStack : e.getPlayer().getInventory().getContents()) {
            if (itemStack == null || itemStack.getType() != Material.EMERALD) continue;
            if (itemStack.getType() == Material.WOOD_HOE) return;
            emeraldCount += itemStack.getAmount();
        }
        if (emeraldCount < 10) return;
        e.getPlayer().getInventory().removeItem(new ItemStack(Material.EMERALD, 10));
        e.getPlayer().getInventory().addItem(new ItemStack(Material.WOOD_HOE));
    }
}
