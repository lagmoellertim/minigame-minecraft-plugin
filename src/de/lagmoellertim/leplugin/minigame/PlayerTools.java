package de.lagmoellertim.leplugin.minigame;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class PlayerTools {
    public static PlayerState getPlayerState(Player player){
        return (PlayerState) player.getMetadata("miniGameState").get(0).value();
    }

    public static MiniGame getPlayerGame(Player player) {
        return (MiniGame) player.getMetadata("miniGameObject").get(0).value();
    }

    public static void setPlayerState(Plugin plugin, Player player, PlayerState playerState) {
        player.setMetadata("miniGameState", new FixedMetadataValue(plugin, playerState));
    }

    public static void setPlayerGame(Plugin plugin, Player player, MiniGame miniGame) {
        player.setMetadata("miniGameObject", new FixedMetadataValue(plugin, miniGame));
    }
}
