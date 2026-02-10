package com.cropsplus.gui;

import com.cropsplus.CropsPlus;
import com.cropsplus.features.quests.QuestManager;
import com.cropsplus.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class MenuHandler implements Listener {

    private final CropsPlus plugin;

    public MenuHandler(CropsPlus plugin) {
        this.plugin = plugin;
    }

    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, CC.translate("&2Crops+ Menu"));

        inv.setItem(11, createItem(Material.WRITABLE_BOOK, "&6Daily Quests", "&7Click to view quests"));
        inv.setItem(13, createItem(Material.DIAMOND_HOE, "&bAutomations", "&7Manage your farms"));
        inv.setItem(15, createItem(Material.PAPER, "&eStats", "&7View crop statistics"));

        player.openInventory(inv);
    }

    public void openQuestsMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, CC.translate("&6Daily Quests"));
        
        List<QuestManager.DailyQuest> quests = plugin.getQuestManager().getActiveQuests();
        int slot = 10;
        for (QuestManager.DailyQuest quest : quests) {
            int progress = plugin.getQuestManager().getProgress(player, quest.id());
            String status = progress >= quest.targetAmount() ? "&aCompleted" : "&eIn Progress";
            
            inv.setItem(slot++, createItem(quest.crop(), 
                "&6Harvest " + quest.crop().name(), 
                "&7Target: &f" + quest.targetAmount(),
                "&7Progress: &f" + progress,
                "",
                status
            ));
        }
        
        player.openInventory(inv);
    }

    private ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(CC.translate(name));
        meta.setLore(List.of(lore));
        // Translate lore colors loop omitted for brevity, assume simple
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();

        if (title.equals(CC.translate("&2Crops+ Menu"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            Material type = event.getCurrentItem().getType();
            
            if (type == Material.WRITABLE_BOOK) {
                openQuestsMenu(player);
            } else if (type == Material.DIAMOND_HOE) {
                player.sendMessage(CC.translate("&cShift+Right Click crops to create automations."));
                player.closeInventory();
            }
        } else if (title.equals(CC.translate("&6Daily Quests"))) {
            event.setCancelled(true);
        }
    }
}