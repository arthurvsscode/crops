package com.cropsplus.features.holograms;

import com.cropsplus.CropsPlus;
import com.cropsplus.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HologramManager {

    private final CropsPlus plugin;
    private BukkitTask task;
    // Map player UUID to their current temporary hologram entity
    private final Map<UUID, TextDisplay> activeHolograms = new HashMap<>();

    public HologramManager(CropsPlus plugin) {
        this.plugin = plugin;
        startTask();
    }

    private void startTask() {
        // Check every 5 ticks for what players are looking at
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateHologramForPlayer(player);
            }
        }, 20L, 5L);
    }

    private void updateHologramForPlayer(Player player) {
        Block target = player.getTargetBlockExact(5, FluidCollisionMode.NEVER);
        TextDisplay current = activeHolograms.get(player.getUniqueId());

        if (target != null && isCrop(target.getType())) {
            if (target.getBlockData() instanceof Ageable ageable) {
                Location holoLoc = target.getLocation().add(0.5, 1.2, 0.5);
                
                // Create if not exists or if looking at different block (simple dist check)
                if (current == null || !current.isValid() || current.getLocation().distanceSquared(holoLoc) > 0.1) {
                    removeHologram(player);
                    spawnHologram(player, holoLoc, ageable, target.getType());
                } else {
                    // Update text
                    updateText(current, ageable, target.getType());
                }
                return;
            }
        }
        
        // If not looking at crop, remove hologram
        removeHologram(player);
    }

    private void spawnHologram(Player player, Location loc, Ageable ageable, Material mat) {
        TextDisplay display = (TextDisplay) loc.getWorld().spawnEntity(loc, EntityType.TEXT_DISPLAY);
        display.setBillboard(Display.Billboard.CENTER);
        display.setVisibleByDefault(false);
        player.showEntity(plugin, display);
        
        // Scale down slightly
        Transformation transform = display.getTransformation();
        transform.getScale().set(0.5f, 0.5f, 0.5f);
        display.setTransformation(transform);

        updateText(display, ageable, mat);
        activeHolograms.put(player.getUniqueId(), display);
    }

    private void updateText(TextDisplay display, Ageable ageable, Material mat) {
        int age = ageable.getAge();
        int max = ageable.getMaximumAge();
        int percent = (int) (((double) age / max) * 100);
        
        String color = percent == 100 ? "&a" : "&e";
        String name = mat.name().replace("_", " ");
        
        display.setText(CC.translate(
            "&6&l" + name + "\n" +
            color + percent + "% Grown"
        ));
    }

    public void removeHologram(Player player) {
        TextDisplay display = activeHolograms.remove(player.getUniqueId());
        if (display != null && display.isValid()) {
            display.remove();
        }
    }

    private boolean isCrop(Material mat) {
        return mat == Material.WHEAT || mat == Material.CARROTS || 
               mat == Material.POTATOES || mat == Material.BEETROOTS || 
               mat == Material.SWEET_BERRY_BUSH;
    }

    public void cleanup() {
        if (task != null) task.cancel();
        activeHolograms.values().forEach(d -> {
            if (d.isValid()) d.remove();
        });
        activeHolograms.clear();
    }
}