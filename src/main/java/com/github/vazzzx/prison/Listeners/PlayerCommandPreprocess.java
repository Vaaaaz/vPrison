package com.github.vazzzx.prison.Listeners;

import com.github.vazzzx.prison.Cache.PlayerAccountCache;
import com.github.vazzzx.prison.Components.PlayerAccount;
import com.github.vazzzx.prison.Prison;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;


@RequiredArgsConstructor
public class PlayerCommandPreprocess implements Listener {

    private final PlayerAccountCache playerAccountCache;


    @EventHandler
    public void asyncPlayerChat(PlayerCommandPreprocessEvent event) {


        Player player = event.getPlayer();
        if (playerAccountCache.getBlockedPlayerCache().containsKey(player.getName())) {
            PlayerAccount playerAccount = playerAccountCache.getBlockedPlayerCache().get(player.getName());
            FileConfiguration config = Prison.config.getConfig();

            if (playerAccount.isBlocked()) {
                if (Prison.allowedCommands.contains(event.getMessage())) return;
                event.setCancelled(true);
                player.sendMessage(config.getString("nao-pode-usar").replace("&", "§"));
            }

        }

    }

}


