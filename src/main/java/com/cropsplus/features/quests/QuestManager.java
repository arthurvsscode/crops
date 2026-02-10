package com.cropsplus.features.quests;

import com.cropsplus.CropsPlus;
import com.cropsplus.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class QuestManager {

    private final CropsPlus plugin;
    private final List<DailyQuest> activeQuests = new ArrayList<>();
    private final Map<UUID, Map<Integer, Integer>> playerProgress = new HashMap<>(); // UUID -> QuestIndex -> Amount
    private final Map<UUID, Set<Integer>> playerCompleted = new HashMap<>();

    public QuestManager(CropsPlus plugin) {
        this.plugin = plugin;
        generateDailyQuests();
        
        // Schedule Midnight Reset (Simulated for this example by a repeating check)
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            // Logic to check system time for midnight could go here
            // For simplicity, we won't implement real-time clock sync logic in this snippet
        }, 1200L, 1200L);
    }

    public void generateDailyQuests() {
        activeQuests.clear();
        Material[] crops = {Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS};
        Random rand = new Random();

        for (int i = 0; i < 4; i++) {
            Material crop = crops[rand.nextInt(crops.length)];
            int amount = (rand.nextInt(5) + 1) * 32; // 32 to 192 items
            activeQuests.add(new DailyQuest(i, crop, amount));
        }
    }

    public void addProgress(Player player, Material crop, int amount) {
        for (DailyQuest quest : activeQuests) {
            if (quest.crop == crop) {
                if (isCompleted(player, quest.id)) continue;

                Map<Integer, Integer> progress = playerProgress.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
                int current = progress.getOrDefault(quest.id, 0);
                current += amount;
                progress.put(quest.id, current);

                if (current >= quest.targetAmount) {
                    completeQuest(player, quest);
                }
            }
        }
    }

    private boolean isCompleted(Player player, int questId) {
        return playerCompleted.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).contains(questId);
    }

    private void completeQuest(Player player, DailyQuest quest) {
        playerCompleted.get(player.getUniqueId()).add(quest.id);
        player.sendMessage(CC.translate("&2[Crops+] &aQuest Completed: Harvest " + quest.targetAmount + " " + quest.crop.name()));
        // Give reward
        player.getInventory().addItem(new ItemStack(Material.EMERALD, 5));
    }

    public List<DailyQuest> getActiveQuests() {
        return activeQuests;
    }
    
    public int getProgress(Player p, int questId) {
         return playerProgress.getOrDefault(p.getUniqueId(), new HashMap<>()).getOrDefault(questId, 0);
    }

    public void save() {
        // Save logic to file/db
    }

    public record DailyQuest(int id, Material crop, int targetAmount) {}
}