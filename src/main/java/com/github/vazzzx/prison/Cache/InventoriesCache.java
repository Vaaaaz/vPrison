package com.github.vazzzx.prison.Cache;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class InventoriesCache {

    private final Map<String, List<ItemStack>> inventories;

    public InventoriesCache() {
        this.inventories = new HashMap<>();
    }
}
