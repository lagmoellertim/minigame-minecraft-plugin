package de.lagmoellertim.leplugin;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldCommandHandler implements CommandExecutor {
    private Server server;

    public WorldCommandHandler (Server server) {
        this.server = server;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        switch (s) {
            case "tpw":
                if(!(commandSender instanceof Player)) {
                    return false;
                }
                handleTPW((Player) commandSender, strings[0]);
                break;
        }
        return true;
    }

    public void handleTPW(Player player, String worldName) {
        Location tpLocation = new Location(server.getWorld(worldName), 0, 100, 0);
        player.teleport(tpLocation);
    }
}
