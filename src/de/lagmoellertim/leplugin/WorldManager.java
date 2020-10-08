package de.lagmoellertim.leplugin;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class WorldManager {
    private Server server;
    private World world;

    public static WorldManager createNewWorld(Server server, String worldName, WorldType worldType, String generatorSettings) {
        WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.type(worldType);
        worldCreator.generatorSettings(generatorSettings);
        World world = worldCreator.createWorld();

        return new WorldManager(server, world);
    }

    public static WorldManager loadWorld(Server server, String worldName) {
        new WorldCreator(worldName).createWorld();
        return new WorldManager(server, server.getWorld(worldName));
    }

    public WorldManager(Server server, World world) {
        this.server = server;
        this.world = world;
    }

    public void delete() {
        unload();
        File worldPath = world.getWorldFolder();
        deleteDirectory(worldPath);
    }

    private boolean deleteDirectory(File path) {
        if(path.exists()) {
            File files[] = path.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return(path.delete());
    }

    private static void copyFileStructure(File source, File target){
        try {
            ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.lock"));
            if(!ignore.contains(source.getName())) {
                if(source.isDirectory()) {
                    if(!target.exists())
                        if (!target.mkdirs())
                            throw new IOException("Couldn't create world directory!");
                    String files[] = source.list();
                    for (String file : files) {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyFileStructure(srcFile, destFile);
                    }
                } else {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private World copyWorld(World originalWorld, String newWorldName) {
        copyFileStructure(originalWorld.getWorldFolder(), new File(Bukkit.getWorldContainer(), newWorldName));
        return new WorldCreator(newWorldName).createWorld();
    }

    public WorldManager copy(String newName) {
        if(server.getWorld(newName) != null) {
            server.unloadWorld(server.getWorld(newName), true);
        }
        World newWorld = copyWorld(world, newName);
        return new WorldManager(server, server.getWorld(newName));
    }

    public void unload() {
        if(world != null) {
            server.unloadWorld(world, true);
        }
    }

    public World getWorld() {
        return world;
    }

    public Location getWorldLocation(double x, double y, double z) {
        return new Location(world, x, y, z);
    }
}
