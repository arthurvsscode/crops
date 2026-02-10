package com.cropsplus;

import com.cropsplus.commands.CropsCommand;
import com.cropsplus.features.automation.AutomationManager;
import com.cropsplus.features.holograms.HologramManager;
import com.cropsplus.features.quests.QuestManager;
import com.cropsplus.gui.MenuHandler;
import com.cropsplus.listeners.PlayerListeners;
import org.bukkit.plugin.java.JavaPlugin;

public class CropsPlus extends JavaPlugin {

    private static CropsPlus instance;
    private HologramManager hologramManager;
    private QuestManager questManager;
    private AutomationManager automationManager;
    private MenuHandler menuHandler;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.hologramManager = new HologramManager(this);
        this.questManager = new QuestManager(this);
        this.automationManager = new AutomationManager(this);
        this.menuHandler = new MenuHandler(this);

        getCommand("crops").setExecutor(new CropsCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerListeners(this), this);
        getServer().getPluginManager().registerEvents(menuHandler, this);

        getLogger().info("Crops+ has been enabled!");
    }

    @Override
    public void onDisable() {
        if (hologramManager != null) hologramManager.cleanup();
        if (automationManager != null) automationManager.saveAll();
        if (questManager != null) questManager.save();
        getLogger().info("Crops+ has been disabled!");
    }

    public static CropsPlus getInstance() { return instance; }
    public HologramManager getHologramManager() { return hologramManager; }
    public QuestManager getQuestManager() { return questManager; }
    public AutomationManager getAutomationManager() { return automationManager; }
    public MenuHandler getMenuHandler() { return menuHandler; }
}