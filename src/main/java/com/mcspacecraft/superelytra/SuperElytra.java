package com.mcspacecraft.superelytra;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.mcspacecraft.superelytra.command.ElytraModeCommand;
import com.mcspacecraft.superelytra.events.PlayerListener;

public class SuperElytra extends JavaPlugin {
    private static SuperElytra plugin;
    public static final Logger logger = Logger.getLogger("Minecraft.SuperElytra");

    private SuperElytraConfig config;

    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        plugin = this;

        config = new SuperElytraConfig(this);
        config.load();

        playerManager = new PlayerManager(this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getCommand("elytra").setExecutor(new ElytraModeCommand(this));
    }

    @Override
    public void onDisable() {
        playerManager.saveSettings();
    }

    public static SuperElytra getPlugin() {
        return plugin;
    }

    public SuperElytraConfig getSuperElytraConfig() {
        return config;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public void logInfoMessage(final String msg, final Object... args) {
        if (args == null || args.length == 0) {
            logger.info(String.format("[%s] %s", getDescription().getName(), msg));
        } else {
            logger.info(String.format("[%s] %s", getDescription().getName(), String.format(msg, args)));
        }
    }

    public void logErrorMessage(final String msg, final Object... args) {
        if (args == null || args.length == 0) {
            logger.severe(String.format("[%s] %s", getDescription().getName(), msg));
        } else {
            logger.severe(String.format("[%s] %s", getDescription().getName(), String.format(msg, args)));
        }
    }
}
