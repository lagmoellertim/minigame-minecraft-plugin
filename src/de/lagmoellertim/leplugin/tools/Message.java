package de.lagmoellertim.leplugin.tools;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Message {
    public static void sendInfo(Player player, String origin, String info) {
        player.sendMessage("["+origin+"] "+info);
    }
    public static void sendWarning(Player player, String origin, String warning) {
        player.sendMessage(ChatColor.YELLOW+"["+origin+"] "+warning);
    }
    public static void sendError(Player player, String origin, String error) {
        player.sendMessage(ChatColor.RED+"["+origin+"] "+error);
    }
}
