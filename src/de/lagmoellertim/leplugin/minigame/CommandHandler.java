package de.lagmoellertim.leplugin.minigame;

import de.lagmoellertim.leplugin.tools.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            switch (s) {
                case "join":
                    join(player, strings[0]);
                    break;
                case "start":
                    start(player);

                case "leave":
                    leave(player);
            }
            return true;
        }
        return false;
    }

    private void join(Player player, String gameID) {
        MiniGame miniGame = MiniGame.getGameByID(gameID);
        if(miniGame == null) {
            Message.sendError(player, "MiniGame", "MiniGame ID not found!");
            return;
        }
        miniGame.join(player);
    }

    private void start(Player player) {
        MiniGame miniGame = PlayerTools.getPlayerGame(player);
        if(miniGame == null) {
            Message.sendError(player, "MiniGame", "You are not in a game!");
            return;
        }
        miniGame.start();
    }

    private void leave(Player player) {
        MiniGame miniGame = PlayerTools.getPlayerGame(player);
        if(miniGame == null) {
            Message.sendError(player, "MiniGame", "You are not in a game!");
            return;
        }
        miniGame.leave(player, false);
    }


}
