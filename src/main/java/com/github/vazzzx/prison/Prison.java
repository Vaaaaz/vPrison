package com.github.vazzzx.prison;

import com.github.vazzzx.prison.Cache.InventoriesCache;
import com.github.vazzzx.prison.Cache.PlayerAccountCache;
import com.github.vazzzx.prison.Commands.Punish;
import com.github.vazzzx.prison.Components.PlayerAccount;
import com.github.vazzzx.prison.Components.RegionPreset;
import com.github.vazzzx.prison.Listeners.PlayerCommandPreprocess;
import com.github.vazzzx.prison.Listeners.PlayerJoin;
import com.github.vazzzx.prison.Listeners.TriggerOnPLayerBreakBlock;
import com.github.vazzzx.prison.Utils.ConfigUtils;
import com.github.vazzzx.prison.Utils.Cuboid;
import com.github.vazzzx.prison.sync.InventoryLoader;
import com.github.vazzzx.prison.sync.PlayerAccountLoader;
import com.redeheavy.heavycore.commons.enums.PlatformType;
import com.redeheavy.heavycore.commons.logging.LoggerFactory;
import com.redeheavy.heavycore.commons.objects.HeavyPlugin;
import com.redeheavy.heavycore.commons.updater.UpdateChecker;
import com.redeheavy.heavycore.platform.bukkit.systems.AutoDownloadDependency;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.List;


@Getter
public class Prison extends JavaPlugin {

    private PlayerAccountCache playerAccountCache;
    private InventoriesCache inventoriesCache;
    private RegionPreset regionPreset;
    private UpdateChecker updateChecker;

    public static Prison plugin;
    public static ConfigUtils config;
    public static HeavyPlugin heavyPlugin;
    public static LoggerFactory logTool;

    public static Location prisonlocation;
    public static List<String> allowedCommands;

    public void onEnable() {
        plugin = this;
        this.playerAccountCache = new PlayerAccountCache();
        this.inventoriesCache = new InventoriesCache();
        this.regionPreset = new RegionPreset();

        heavyPlugin = new HeavyPlugin(
                "vPrison",
                "com.github.vazzzx.prison.Prison",
                PlatformType.BUKKIT,
                "1",
                "vazzzx"
        );

        logTool = new LoggerFactory(heavyPlugin, "&c[vPrison]");

        // ql o nome do rep?
        updateChecker = new UpdateChecker(this, "Vazzzx", "vPrison");
        updateChecker.setToken("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");
        updateChecker.check();

        boolean need_restart = false;
        val url = "https://github.com/Vazzzx/vPrison/releases/download/latest/vPrison.jar";

        if (updateChecker.canUpdate()) {
            logTool.info("==============================================");
            logTool.info("Uma nova versão do plugin está disponível!");
            logTool.info(" - Baixe utilizando o link abaixo: ");
            logTool.info("    " + url);
            logTool.info("==============================================");
        } else {
            logTool.success("GG! Você está usando a última versão do plugin.");
        }

        Bukkit.getConsoleSender().sendMessage(new String[]{
                "",
                "§fO plugin §bvPrison §ffoi iniciado com sucesso!",
                "              §fVersão: §b1.0",
                ""
        });

        registerCommands();
        registerEvents();
        archives();

        new PlayerAccountLoader(playerAccountCache).load();
        new InventoryLoader(inventoriesCache).loadInventories();

        try {
            prisonlocation = Location.deserialize(config.getConfig().getConfigurationSection("loc").getValues(false));
        } catch (Exception var) {
            Bukkit.getConsoleSender().sendMessage("§cA localização da prisão não foi configurada, use /prison setloc para setar!");
        }

        try {
            allowedCommands = config.getConfig().getStringList("comandos-permitidos");
        } catch (Exception ignored) {
        }

        try {
            Cuboid cuboid = new Cuboid(config.getConfig().getConfigurationSection("regiao-blocos").getValues(false));
            regionPreset.setCuboid(cuboid);
        } catch (Exception var) {
            Bukkit.getConsoleSender().sendMessage("§cNão foi possivel carregar a região dos blocos");
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (regionPreset.getCuboid() == null) return;
                for (Block block : regionPreset.getCuboid().getBlocks()) {
                    block.setType(Material.OBSIDIAN);
                }
            }
        }.runTaskTimer(this, 0L, 20 * 15);

    }

    public void onDisable() {

    }

    public void registerCommands() {
        getCommand("prison").setExecutor(new Punish(playerAccountCache, this));
    }

    public void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new PlayerCommandPreprocess(playerAccountCache), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(playerAccountCache), this);
        Bukkit.getPluginManager().registerEvents(new TriggerOnPLayerBreakBlock(this), this);
    }


    public void archives() {
        config = new ConfigUtils(this, "config.yml");
        config.saveDefaultConfig();
        createFile(this, "", false);

    }

    public void createFile(Prison main, String fileName, boolean isFile) {
        try {
            File file = new File(main.getDataFolder() + File.separator + fileName);
            if (isFile) file.createNewFile();
            else if (!file.exists()) file.mkdirs();
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }


}