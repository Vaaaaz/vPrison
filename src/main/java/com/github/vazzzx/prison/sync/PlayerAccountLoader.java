package com.github.vazzzx.prison.sync;

import com.github.vazzzx.prison.Cache.PlayerAccountCache;
import com.github.vazzzx.prison.Components.PlayerAccount;
import com.github.vazzzx.prison.Prison;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;

@RequiredArgsConstructor
public class PlayerAccountLoader {

    private final PlayerAccountCache playerAccountCache;

    public void load() {
        FileConfiguration config = Prison.config.getConfig();

        try {
            config.getConfigurationSection("Accounts").getKeys(false).forEach(playerName -> {
                PlayerAccount playerAccount = new PlayerAccount(playerName);
                playerAccount.setBlocked(config.getBoolean("Accounts." + playerName + ".isBlocked"));
                playerAccount.setSentence(config.getInt("Accounts." + playerName + ".sentence"));
                playerAccount.setRemainBlocks(config.getInt("Accounts." + playerName + ".remainBlocks"));

                playerAccountCache.getBlockedPlayerCache().put(
                    playerName,
                    playerAccount
                );
            });
        } catch (Exception ignored) { }
    }
}
