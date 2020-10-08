package de.lagmoellertim.leplugin.minigame.games;

import de.lagmoellertim.leplugin.WorldManager;
import de.lagmoellertim.leplugin.minigame.LeaveType;
import de.lagmoellertim.leplugin.minigame.MiniGame;
import de.lagmoellertim.leplugin.minigame.MiniGameType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class DontFall extends MiniGame implements Listener {
    boolean gameArenaCreated = false;
    WorldManager arena;
    private ArrayList<Block> blocksToRemove;
    private ArrayList<Player> activePlayers;

    public DontFall(Server server, Plugin plugin, String gameID, int minSlots, int maxSlots) {
        super(server, plugin, MiniGameType.DontFall, gameID, minSlots, maxSlots);
        this.blocksToRemove = new ArrayList<>();
        this.activePlayers = new ArrayList<>();
    }

    @Override
    public boolean join(Player player) {
        boolean join = super.join(player);
        if(join) {
            if(!gameArenaCreated) {
                WorldManager wm = WorldManager.loadWorld(getServer(), "DF_01");
                arena = wm.copy("DF_01_"+getGameID());
                gameArenaCreated = true;
            }
            player.teleport(getRandomLocation());
            activePlayers.add(player);
        }

        return join;
    }

    private Location getRandomLocation() {
        return arena.getWorldLocation(getRandomNumber(-85, 6), 17, getRandomNumber(-86, 5));
    }

    private int getRandomNumber(int min, int max) {
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }

    @Override
    public boolean start() {
        return super.start();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(getRegisteredPlayers().contains(event.getPlayer())) {
            Player player = event.getPlayer();
            if(!isStarted()) {
                // Make Player not move
                if(!event.getFrom().getBlock().equals(event.getTo().getBlock())) {
                    event.setCancelled(true);
                }
            } else {
                double y = player.getLocation().getY();
                if(player.getLocation().getBlock().getType().equals(Material.WATER)) {
                    player.setGameMode(GameMode.SPECTATOR);
                    if(activePlayers.size() == 1 && getRegisteredPlayers().size() == 1) {
                        player.sendTitle(ChatColor.RED+"Game over!","");
                        reset();
                    } else if(activePlayers.size() == 2) {
                        activePlayers.remove(player);
                        activePlayers.get(0).sendTitle(ChatColor.GREEN+"You won!","");
                        for (Player currentPlayer:
                                getRegisteredPlayers()) {
                            if(currentPlayer.equals(activePlayers.get(0))) {
                                currentPlayer.sendTitle(ChatColor.RED+"You lost!","");
                            }
                        }
                        reset();
                    } else {
                        activePlayers.remove(player);
                    }

                    return;
                }
                if(y == (int) y) {
                    Location blockLocation = player.getLocation().add(0, -1, 0);
                    Block block = blockLocation.getBlock();
                    if(!blocksToRemove.contains(block)) {
                        BukkitRunnable delayedBlockRemoval = new BukkitRunnable() {
                            @Override
                            public void run() {
                                block.setType(Material.AIR);
                                blocksToRemove.remove(block);
                            }
                        };
                        delayedBlockRemoval.runTaskLater(getPlugin(), 7);
                        blocksToRemove.add(block);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSnowBall(ProjectileHitEvent event) {
        if(isStarted()) {
            if(event.getHitBlock().getLocation().getWorld().equals(arena.getWorld())) {
                event.getHitBlock().setType(Material.AIR);
            }
        }
    }

    @Override
    public LeaveType leave(Player player, boolean force) {
        return LeaveType.NOT_LEFT;//super.leave(player, force);
    }



    @Override
    public void reset() {
        for (Player currentPlayer:
                getRegisteredPlayers()) {
            currentPlayer.teleport(getServer().getWorld("world").getSpawnLocation());
            currentPlayer.setGameMode(GameMode.SURVIVAL);
        }
        super.reset();

        arena.delete();
        blocksToRemove.clear();
        activePlayers.clear();
        gameArenaCreated = false;
        arena = null;
    }
}
