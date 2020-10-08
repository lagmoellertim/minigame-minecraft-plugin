package de.lagmoellertim.leplugin;
import de.lagmoellertim.leplugin.minigame.CommandHandler;
import de.lagmoellertim.leplugin.minigame.MiniGame;
import de.lagmoellertim.leplugin.minigame.games.DontFall;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
    @Override
    public void onEnable(){
        //WorldCreator wc = new WorldCreator("world name");
        //wc.environment(World.Environment;
        // getServer().getPluginManager().registerEvents(new StopMovement(this), this);
        WorldCommandHandler wch = new WorldCommandHandler(getServer());
        //WorldManager worldManager = WorldManager.createNewWorld(getServer(), "DF_01", WorldType.FLAT, "");
        //worldManager.copy("TimLucaAuch");
        //getCommand("create-world").setExecutor(worldManager);
        getCommand("tpw").setExecutor(wch);

        MiniGame dontFall1 = new DontFall(getServer(), this, "df01",1,10);
        getServer().getPluginManager().registerEvents(dontFall1, this);
        CommandHandler commandHandler = new CommandHandler();
        getCommand("join").setExecutor(commandHandler);
        getCommand("start").setExecutor(commandHandler);
        getCommand("leave").setExecutor(commandHandler);

        //DontFall dontFall = new DontFall(getServer(), this);
        //getCommand("dontfall").setExecutor(dontFall);
        //getServer().getPluginManager().registerEvents(dontFall, this);

        getCommand("r").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                getServer().reload();
                for (Player p:
                     getServer().getOnlinePlayers()) {
                    p.sendTitle("","Reload complete");
                }
                return true;
            }
        });
    }
    @Override
    public void onDisable() {

    }
}
