package com.cropsplus.utils;

import org.bukkit.ChatColor;

public class CC {
    public static String translate(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}