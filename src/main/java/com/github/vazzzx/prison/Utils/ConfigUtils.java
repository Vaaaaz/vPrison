package com.github.vazzzx.prison.Utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigUtils {

    private final String fileName;
    private final JavaPlugin plugin;
    private final File configFile;
    private FileConfiguration fileConfiguration;

    public ConfigUtils(JavaPlugin plugin, String fileName) {
        if (plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }
        if (!plugin.isEnabled()) {
            throw new IllegalArgumentException("plugin must be enabled");
        }
        this.plugin = plugin;
        this.fileName = fileName;
        File dataFolder = plugin.getDataFolder();
        if (dataFolder == null) {
            throw new IllegalStateException();
        }
        this.configFile = new File(plugin.getDataFolder(), fileName);
    }

    public void saveDefaultConfig() {
        if (!this.configFile.exists())
            this.plugin.saveResource(this.fileName, false);
    }


    public void reloadConfig() {
        this.fileConfiguration = (FileConfiguration) YamlConfiguration.loadConfiguration(this.configFile);

    }


    public FileConfiguration getConfig() {
        if (this.fileConfiguration == null) {
            reloadConfig();
        }
        return this.fileConfiguration;
    }

    public void save() {
        try {
            this.fileConfiguration.save(configFile);
            this.fileConfiguration.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            Bukkit.getConsoleSender().sendMessage("§cOcorreu um erro ao salvar a configuração!");
        }
    }


}
