package de.lagmoellertim.leplugin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DontFall implements CommandExecutor, Listener {
    private ArrayList<Player> playersInGame;
    private ArrayList<Block> blocksToRemove;
    private ArrayList<Player> activePlayers;
    private boolean gameArenaCreated = false;
    private boolean gameStarted = false;
    private Server server;
    private Plugin plugin;
    private WorldManager arena;

    public DontFall(Server server, Plugin plugin) {
        this.server = server;
        this.plugin = plugin;
        this.playersInGame = new ArrayList<>();
        this.blocksToRemove = new ArrayList<>();
        this.activePlayers = new ArrayList<>();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if(strings[0].equals("join")) {
                if(!gameStarted) {
                    if(!gameArenaCreated) {
                        WorldManager wm = WorldManager.loadWorld(server, "DF_01");
                        arena = wm.copy("DF_01_tmp");
                        gameArenaCreated = true;
                    }
                    player.teleport(getRandomLocation());
                    playersInGame.add(player);
                    activePlayers.add(player);
                } else {
                    player.sendMessage(ChatColor.RED + "Game already started!");
                }
            }
            if(strings[0].equals("start")) {
                if(gameStarted) {
                    player.sendMessage(ChatColor.RED + "Game already started!");
                } else if(!playersInGame.contains(player)) {
                    player.sendMessage(ChatColor.RED + "You're not part of the game");
                } else {
                    BukkitRunnable timer = new BukkitRunnable() {
                        private int count = 5;
                        @Override
                        public void run() {
                            if(count == 0) {
                                for (Player currentPlayer:
                                        playersInGame) {
                                    currentPlayer.sendTitle(ChatColor.GREEN+"Go!", "Game started");
                                }
                                cancel();
                                gameStarted = true;
                            }
                            for (Player currentPlayer:
                                 playersInGame) {
                                currentPlayer.sendTitle(ChatColor.GREEN+""+count, "Game starting");
                            }
                            count--;
                        }
                    };
                    timer.runTaskTimer(plugin, 10, 20);
                }
            }
        }
        return false;
    }

    private Location getRandomLocation() {
        return arena.getWorldLocation(getRandomNumber(-85, 6), 17, getRandomNumber(-86, 5));
    }

    private int getRandomNumber(int min, int max) {
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(playersInGame.contains(event.getPlayer())) {
            Player player = event.getPlayer();
            if(!gameStarted) {
                // Make Player not move
                if(!event.getFrom().getBlock().equals(event.getTo().getBlock())) {
                    event.setCancelled(true);
                }
            } else {
                double y = player.getLocation().getY();
                if(player.getLocation().getBlock().getType().equals(Material.WATER)) {
                    player.setGameMode(GameMode.SPECTATOR);
                    if(activePlayers.size() == 1 && playersInGame.size() == 1) {
                        player.sendTitle(ChatColor.RED+"Game over!","");
                        resetGame();
                    } else if(activePlayers.size() == 2) {
                        activePlayers.remove(player);
                        activePlayers.get(0).sendTitle(ChatColor.GREEN+"You won!","");
                        for (Player currentPlayer:
                             playersInGame) {
                            if(currentPlayer.equals(activePlayers.get(0))) {
                                currentPlayer.sendTitle(ChatColor.RED+"You lost!","");
                            }
                        }
                        resetGame();
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
                        delayedBlockRemoval.runTaskLater(plugin, 7);
                        blocksToRemove.add(block);
                    }
                }
                player.sendMessage(player.getLocation().getBlock().getType().toString());
            }
        }
    }

    @EventHandler
    public void onSnowBall(ProjectileHitEvent event) {
        if(gameStarted) {
            if(event.getHitBlock().getLocation().getWorld().equals(arena.getWorld())) {
                event.getHitBlock().setType(Material.AIR);
            }
        }
    }

    private void resetGame() {
        for (Player currentPlayer:
             playersInGame) {
            currentPlayer.teleport(server.getWorld("world").getSpawnLocation());
            currentPlayer.setGameMode(GameMode.SURVIVAL);
        }
        arena.delete();
        playersInGame.clear();
        blocksToRemove.clear();
        activePlayers.clear();
        gameArenaCreated = false;
        gameStarted = false;
        arena = null;
    }
}
