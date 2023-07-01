package com.github.vazzzx.prison.Listeners;

import com.github.vazzzx.prison.Components.PlayerAccount;
import com.github.vazzzx.prison.Prison;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class TriggerOnPLayerBreakBlock implements Listener {

    private final Prison prison;

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        PlayerAccount playerAccount = prison.getPlayerAccountCache().getBlockedPlayerCache().get(player.getName());
        Map<String, List<ItemStack>> inventories = prison.getInventoriesCache().getInventories();
        FileConfiguration config = Prison.config.getConfig();

        if (playerAccount.isBlocked()) {
            if (block.getType() != Material.OBSIDIAN) {
                player.sendMessage("§cVocê não pode quebrar outros blocos, apenas obsidians!");
                return;
            }

            event.setCancelled(true);
            block.setType(Material.AIR);
            playerAccount.setRemainBlocks(playerAccount.getRemainBlocks() - 1);

            if (playerAccount.getRemainBlocks() == 0) {
                World world = player.getWorld();
                Location spawnLocation = world.getSpawnLocation();

                playerAccount.setBlocked(false);
                playerAccount.setSentence(0);
                playerAccount.setRemainBlocks(0);


                player.sendMessage(config.getString("removido-prison").replace("&", "§"));
                player.teleport(spawnLocation);
                this.saveAccount(playerAccount);
                this.deleteInventory(player);

                if (inventories.containsKey(player.getName())) {
                    for (ItemStack itemStack : inventories.get(player.getName())) {
                        if (itemStack == null || itemStack.getType() == Material.AIR) continue;
                        player.getInventory().addItem(itemStack);
                    }
                    inventories.remove(player.getName());
                }
                return;
            }

            player.sendMessage(config.getString("quebrou-ob").replace("{faltando}", String.valueOf(playerAccount.getRemainBlocks())).replace("&", "§"));
            saveAccount(playerAccount);
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

    private void deleteInventory(Player player) {
        FileConfiguration config = Prison.config.getConfig();

        config.set("Inventories." + player.getName(), null);
        Prison.config.save();
        Prison.config.reloadConfig();
    }
}
