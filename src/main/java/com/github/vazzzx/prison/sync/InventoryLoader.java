package com.github.vazzzx.prison.sync;

import com.github.vazzzx.prison.Cache.InventoriesCache;
import com.github.vazzzx.prison.Prison;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class InventoryLoader {

    private final InventoriesCache inventoriesCache;

    public void loadInventories() {
        try {
            FileConfiguration config = Prison.config.getConfig();

            config.getConfigurationSection("Inventories")
                .getKeys(false)
                .forEach(inventoryOwner -> {
                    List<ItemStack> inventory = new ArrayList<>();
                    inventoriesCache.getInventories().put(inventoryOwner, inventory);

                    config.getConfigurationSection("Inventories." + inventoryOwner + ".items")
                        .getKeys(false)
                        .forEach(item -> {
                            inventory.add(
                                ItemStack.deserialize(
                                    config.getConfigurationSection("Inventories." + inventoryOwner + ".items." + item)
                                        .getValues(false)
                                )
                            );
                        });

                });
        } catch (Exception var) {
            Bukkit.getConsoleSender().sendMessage("§c[vPrison] Nenhum inventario foi carregado da config!");
        }
    }
}
