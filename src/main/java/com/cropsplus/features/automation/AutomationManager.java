package com.cropsplus.features.automation;

import com.cropsplus.CropsPlus;
import com.cropsplus.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;

import java.util.*;

public class AutomationManager {

    private final CropsPlus plugin;
    private final Map<UUID, List<FarmField>> playerFarms = new HashMap<>();

    public AutomationManager(CropsPlus plugin) {
        this.plugin = plugin;
        startAutomationTask();
    }

    private void startAutomationTask() {
        // Every 60 seconds
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (List<FarmField> fields : playerFarms.values()) {
                for (FarmField field : fields) {
                    processField(field);
                }
            }
        }, 100L, 20L * 60L);
    }

    private void processField(FarmField field) {
        for (Location loc : field.blocks) {
            Block b = loc.getBlock();
            if (b.getType() == field.cropType && b.getBlockData() instanceof Ageable ageable) {
                if (ageable.getAge() == ageable.getMaximumAge()) {
                    // Harvest
                    b.breakNaturally();
                    // Replant
                    b.setType(field.cropType);
                }
            }
        }
    }

    public void createField(Player player, Block startBlock) {
        if (playerFarms.getOrDefault(player.getUniqueId(), new ArrayList<>()).size() >= 10) {
            player.sendMessage(CC.translate("&cMax automations reached (10)."));
            return;
        }

        // Simple Flood Fill to find connected crops
        List<Location> connected = new ArrayList<>();
        Queue<Location> queue = new LinkedList<>();
        Set<Location> visited = new HashSet<>();
        
        Material type = startBlock.getType();
        queue.add(startBlock.getLocation());
        visited.add(startBlock.getLocation());

        while (!queue.isEmpty()) {
            Location current = queue.poll();
            connected.add(current);

            if (connected.size() > 100) break; // Limit size per field

            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && z == 0) continue;
                    Location next = current.clone().add(x, 0, z);
                    if (!visited.contains(next) && next.getBlock().getType() == type) {
                        visited.add(next);
                        queue.add(next);
                    }
                }
            }
        }

        FarmField field = new FarmField(type, connected);
        playerFarms.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(field);
        player.sendMessage(CC.translate("&aAutomation created with " + connected.size() + " blocks."));
    }

    public void saveAll() {
        // Save logic
    }

    public List<FarmField> getFields(UUID uuid) {
        return playerFarms.getOrDefault(uuid, Collections.emptyList());
    }

    public record FarmField(Material cropType, List<Location> blocks) {}
}