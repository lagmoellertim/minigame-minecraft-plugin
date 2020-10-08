package de.lagmoellertim.leplugin.minigame;

import de.lagmoellertim.leplugin.tools.Message;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MiniGame implements Listener {
    Server server;
    Plugin plugin;
    MiniGameType miniGameType;
    String gameID;
    List<Player> registeredPlayers;
    boolean started = false;
    int minSlots = 1;
    int maxSlots = -1;

    static HashMap<String, MiniGame> registeredGames = new HashMap<>();

    public MiniGame(Server server, Plugin plugin, MiniGameType miniGameType, String gameID, int minSlots, int maxSlots) {
        this.server = server;
        this.plugin = plugin;
        this.miniGameType = miniGameType;
        this.gameID = gameID;
        this.minSlots = minSlots;
        this.maxSlots = maxSlots;
        registeredGames.put(gameID, this);
        registeredPlayers = new ArrayList<>();
    }

    public static MiniGame getGameByID(String id) {
        if(registeredGames.containsKey(id)) {
            return registeredGames.get(id);
        }
        return null;
    }

    public boolean join(Player player) {
        MiniGame currentGame = PlayerTools.getPlayerGame(player);

        if(started) {
            Message.sendError(player, "MiniGame", "Game already started!");
            return false;
        }

        if(currentGame != null) {
            LeaveType leaveType = currentGame.leave(player, false);
            if(leaveType == LeaveType.NOT_LEFT) {
                Message.sendError(player, "MiniGame", "You can't leave your current game peacefully!");
                return false;
            }
        }

        if(playerCountInSlotRange(registeredPlayers.size()+1)) {
            registerPlayer(player);
            return true;
        } else {
            Message.sendError(player, "MiniGame", "This MiniGame is full");
            return false;
        }
    }

    public boolean start() {
        if(started) {
            return false;
        }

        BukkitRunnable timer = new BukkitRunnable() {
            private int count = 5;
            @Override
            public void run() {
                if(playerCountInSlotRange(registeredPlayers.size())) {
                    if(count == 0) {
                        for (Player player:
                                registeredPlayers) {
                            player.sendTitle(ChatColor.GREEN+"Go!", "Game started");
                        }
                        started = true;
                        cancel();
                        return;
                    }
                    for (Player player:
                            registeredPlayers) {
                        player.sendTitle(ChatColor.GREEN+""+count, "Game starting");
                    }
                    count--;
                } else {
                        for (Player player:
                                registeredPlayers) {
                            player.sendTitle(ChatColor.RED+"Aborted", "Not enough players");
                        }
                        cancel();
                        return;

                }
            }
        };
        timer.runTaskTimer(plugin, 10, 20);

        return started;
    }

    private boolean playerCountInSlotRange(int playerCount) {
        if(playerCount <= maxSlots || maxSlots < 0) {
            if(playerCount >= minSlots) {
                return true;
            }
        }
        return false;
    }
    public LeaveType leave(Player player, boolean force) {
        if(isPlayerRegistered(player)) {
            if(PlayerTools.getPlayerState(player) == PlayerState.WAITING) {
                unregisterPlayer(player);
                return LeaveType.DEFAULT_LEFT;
            } else {
                if(force) {
                    unregisterPlayer(player);
                    return LeaveType.FORCE_LEFT;
                }
                Message.sendError(player,"MiniGame", "You need to force leave to exit a running game!");
            }
        }
        return LeaveType.NOT_LEFT;
    }

    public void reset() {
        for (Player player:
                registeredPlayers) {
            unregisterPlayer(player);
            started = false;
        }
    }

    private void registerPlayer(Player player) {
        registeredPlayers.add(player);
        PlayerTools.setPlayerState(plugin, player, PlayerState.WAITING);
        PlayerTools.setPlayerGame(plugin, player, this);

    }

    private void unregisterPlayer(Player player) {
        registeredPlayers.remove(player);
        PlayerTools.setPlayerState(plugin, player, PlayerState.IDLE);
        PlayerTools.setPlayerGame(plugin, player, null);
    }



    private boolean isPlayerRegistered(Player player) {
        return registeredPlayers.contains(player);
    }

    public Server getServer() {
        return server;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public List<Player> getRegisteredPlayers() {
        return registeredPlayers;
    }

    public boolean isStarted() {
        return started;
    }

    public int getMinSlots() {
        return minSlots;
    }

    public int getMaxSlots() {
        return maxSlots;
    }

    public String getGameID() {
        return gameID;
    }

    @EventHandler
    public void setDefaultArgsOnJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerTools.setPlayerGame(plugin, player, null);
        PlayerTools.setPlayerState(plugin, player, PlayerState.IDLE);
    }
}
