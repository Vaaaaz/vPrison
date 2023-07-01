package com.github.vazzzx.prison.Listeners;

import com.github.vazzzx.prison.Cache.PlayerAccountCache;
import com.github.vazzzx.prison.Components.PlayerAccount;
import com.github.vazzzx.prison.Prison;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;


@RequiredArgsConstructor
public class PlayerJoin implements Listener {

    private final PlayerAccountCache playerAccountCache;


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (!(playerAccountCache.getBlockedPlayerCache().containsKey(player.getName()))) {
            PlayerAccount playerAccount = new PlayerAccount(player.getName());
            playerAccountCache.getBlockedPlayerCache().put(player.getName(), playerAccount);
            this.saveAccount(playerAccount);
            return;
        }

        PlayerAccount playerAccount = playerAccountCache.getBlockedPlayerCache().get(player.getName());

        if (playerAccount.isBlocked()) {
            player.getInventory().clear();
            player.teleport(Prison.prisonlocation);
            player.sendMessage(Prison.config.getConfig().getString("faltaob").replace("{quantidade}", String.valueOf(playerAccount.getRemainBlocks())));

            boolean activeItem = Prison.config.getConfig().getBoolean("ativar-item");

            if (activeItem) {
                player.getInventory().addItem(ItemStack.deserialize(Prison.config.getConfig().getConfigurationSection("item").getValues(false)));
                player.sendMessage(Prison.config.getConfig().getString("entrar-com-item"));
            }
        }

    }

    private void saveAccount(PlayerAccount playerAccount) {
        FileConfiguration config = Prison.config.getConfig();

        config.set("Accounts." + playerAccount.getPlayerName() + ".isBlocked", playerAccount.isBlocked());
        config.set("Accounts." + playerAccount.getPlayerName() + ".sentence", playerAccount.getSentence());
        config.set("Accounts." + playerAccount.getPlayerName() + ".remainBlocks", playerAccount.getRemainBlocks());

        Prison.config.save();
        Prison.config.reloadConfig();
    }

}
