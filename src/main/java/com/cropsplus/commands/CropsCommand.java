package com.cropsplus.commands;

import com.cropsplus.CropsPlus;
import com.cropsplus.utils.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CropsCommand implements CommandExecutor {

    private final CropsPlus plugin;

    public CropsCommand(CropsPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                plugin.getMenuHandler().openMainMenu(player);
            } else {
                sender.sendMessage(CC.translate("&cPlayers only."));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("clearholo")) {
            if (!sender.hasPermission("crops.admin")) {
                sender.sendMessage(CC.translate(plugin.getConfig().getString("messages.no-permission")));
                return true;
            }
            plugin.getHologramManager().cleanup();
            sender.sendMessage(CC.translate(plugin.getConfig().getString("messages.hologram-cleared")));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("crops.admin")) {
                sender.sendMessage(CC.translate(plugin.getConfig().getString("messages.no-permission")));
                return true;
            }
            plugin.reloadConfig();
            sender.sendMessage(CC.translate(plugin.getConfig().getString("messages.reload")));
            return true;
        }

        return false;
    }
}