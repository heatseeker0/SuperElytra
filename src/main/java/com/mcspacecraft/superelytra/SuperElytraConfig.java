package com.mcspacecraft.superelytra;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.mcspacecraft.superelytra.util.MessageUtils;

public class SuperElytraConfig {
    private final SuperElytra plugin;
    private FileConfiguration config;

    private boolean debug = false;

    private Map<String, String> messages = new HashMap<>();

    private int chargeUpTicks;
    private double speedMultiplier;
    private double launchMultiplier;

    private double enderpearlVelocityMultiplier;
    private double enderpearlMaxVelocity;
    private int enderpearlUseDelay;

    private int elytraXpPerDamage;

    private boolean defaultEnahancedFlight;

    public SuperElytraConfig(SuperElytra plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.saveDefaultConfig();
        applyConfig();
    }

    private void applyConfig() {
        plugin.reloadConfig();

        config = plugin.getConfig();
        debug = config.getBoolean("debug", false);

        chargeUpTicks = config.getInt("chargeup-ticks", 60);
        speedMultiplier = config.getDouble("speed-multiplier", 1.0);
        launchMultiplier = config.getDouble("launch-multiplier", 1.0);

        enderpearlVelocityMultiplier = config.getDouble("enderpearl-velocity-multiplier", 1.3);
        enderpearlMaxVelocity = config.getDouble("enderpearl-max-velocity", 5);
        enderpearlUseDelay = config.getInt("enderpearl-use-delay", 30);

        elytraXpPerDamage = config.getInt("elytra-xp-per-damage", 10);

        defaultEnahancedFlight = config.getBoolean("default-enhanced-flight", true);

        messages.clear();
        for (String msgKey : config.getConfigurationSection("messages").getKeys(false)) {
            messages.put(msgKey, MessageUtils.parseColors(config.getString("messages." + msgKey)));
        }
    }

    public boolean getDebug() {
        return debug;
    }

    public int getChargeUpTicks() {
        return chargeUpTicks;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public double getLaunchMultiplier() {
        return launchMultiplier;
    }

    public double getEnderpearlVelocityMultiplier() {
        return enderpearlVelocityMultiplier;
    }

    public double getEnderpearlMaxVelocity() {
        return enderpearlMaxVelocity;
    }

    public int getEnderpearlUseDelay() {
        return enderpearlUseDelay;
    }

    public int getElytraXpPerDamage() {
        return elytraXpPerDamage;
    }

    public boolean isDefaultEnhancedFlight() {
        return defaultEnahancedFlight;
    }

    public FileConfiguration getRawConfig() {
        return config;
    }

    public String getMessage(final String key) {
        if (messages.containsKey(key)) {
            return messages.get(key);
        }
        final String errorMsg = "No message text in config.yml for " + key;
        plugin.logErrorMessage(errorMsg);
        return errorMsg;
    }
}
