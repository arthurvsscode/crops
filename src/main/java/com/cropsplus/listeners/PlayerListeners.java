package com.cropsplus.listeners;

import com.cropsplus.CropsPlus;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerListeners implements Listener {

    private final CropsPlus plugin;

    public PlayerListeners(CropsPlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        if (isCrop(type) && event.getBlock().getBlockData() instanceof Ageable ageable) {
            if (ageable.getAge() == ageable.getMaximumAge()) {
                // Track quest progress
                plugin.getQuestManager().addProgress(event.getPlayer(), type, 1);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction().isRightClick() && event.getPlayer().isSneaking()) {
            if (event.getClickedBlock() != null && isCrop(event.getClickedBlock().getType())) {
                 // Create automation if holding specific tool or command mode (Simplified to sneak-right-click for now)
                 // Realistically, would use a specific item or command toggle
                 // plugin.getAutomationManager().createField(event.getPlayer(), event.getClickedBlock());
            }
        }
    }
    
    private boolean isCrop(Material mat) {
        return mat == Material.WHEAT || mat == Material.CARROTS || 
               mat == Material.POTATOES || mat == Material.BEETROOTS || 
               mat == Material.SWEET_BERRY_BUSH;
    }
}