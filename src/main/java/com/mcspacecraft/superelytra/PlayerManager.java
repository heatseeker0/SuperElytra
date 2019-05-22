package com.mcspacecraft.superelytra;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.mcspacecraft.superelytra.util.ItemCooldownUtil;
import com.mcspacecraft.superelytra.util.SLAPI;

public class PlayerManager {
    public static final String SETTINGS_FILE = "elytra_settings.dat";

    private final SuperElytra plugin;

    private final double BASE_SPEED = 0.05;
    private final double BASE_LAUNCH = 3;

    private Map<Player, Integer> chargeTime = new WeakHashMap<>();
    private Set<UUID> enabled = new HashSet<>();

    public PlayerManager(final SuperElytra plugin) {
        this.plugin = plugin;
        loadSettings();
        Bukkit.getScheduler().runTaskTimer(plugin, new ChargeTicker(), 0, 1);
    }

    public void saveSettings() {
        try {
            SLAPI.save(enabled, plugin.getDataFolder() + "/" + SETTINGS_FILE);
            plugin.logInfoMessage("Saved %d elytra settings to disk.", enabled.size());
        } catch (Exception e) {
            plugin.logErrorMessage("Error saving elytra settings to disk: ", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadSettings() {
        try {
            enabled = (Set<UUID>) SLAPI.load(plugin.getDataFolder() + "/" + SETTINGS_FILE);
            plugin.logInfoMessage("Loaded %d saved elytra settings from disk.", enabled.size());
        } catch (Exception e) {
            plugin.logErrorMessage("Error loading elytra settings from disk: ", e.getMessage());
        }
    }

    public void enableSuperElytra(final Player player) {
        enabled.add(player.getUniqueId());
        saveSettings();
    }

    public void disableSuperElytra(final Player player) {
        enabled.remove(player.getUniqueId());
        saveSettings();
    }

    public boolean isEnabled(final Player player) {
        return enabled.contains(player.getUniqueId());
    }

    public void startCharging(final Player player) {
        if (!isEnabled(player) || !wearsElytra(player)) {
            return;
        }

        chargeTime.put(player, 0);
    }

    private boolean wearsElytra(final Player player) {
        ItemStack chestPlate = player.getEquipment().getChestplate();
        return chestPlate != null && chestPlate.getType() == Material.ELYTRA;
    }

    public void stopCharging(final Player player) {
        if (isEnabled(player) && chargeTime.getOrDefault(player, 0) >= plugin.getSuperElytraConfig().getChargeUpTicks() && wearsElytra(player)) {
            Location loc = player.getLocation();
            Vector dir = loc.getDirection().add(new Vector(0, BASE_LAUNCH * plugin.getSuperElytraConfig().getLaunchMultiplier(), 0));

            player.setVelocity(player.getVelocity().add(dir));
            loc.getWorld().spawnParticle(Particle.CLOUD, loc, 30, 0.5F, 0.5F, 0.5F, 0.0F);
            player.playSound(loc, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.1F, 2.0F);
        }
        chargeTime.remove(player);
    }

    public void cancelCharging(final Player player) {
        chargeTime.remove(player);
    }

    public void enhancedGlide(Player player) {
        if (!isEnabled(player) || !wearsElytra(player)) {
            return;
        }

        Location loc = player.getLocation();
        if (loc.getBlockY() > 200) {
            return;
        }

        Vector unitVector = new Vector(0, player.getLocation().getDirection().getY(), 0);
        player.setVelocity(player.getVelocity().add(unitVector.multiply(BASE_SPEED * plugin.getSuperElytraConfig().getSpeedMultiplier())));
    }

    public void boostFlight(Player player) {
        if (!isEnabled(player) || !wearsElytra(player)) {
            return;
        }

        Location loc = player.getLocation();
        if (loc.getBlockY() > 200) {
            return;
        }

        Vector velocity = player.getVelocity();
        double maxVelocity = plugin.getSuperElytraConfig().getEnderpearlMaxVelocity();
        if (velocity.getX() > maxVelocity || velocity.getY() > maxVelocity || velocity.getZ() > maxVelocity) {
            return;
        }

        player.setVelocity(player.getVelocity().multiply(plugin.getSuperElytraConfig().getEnderpearlVelocityMultiplier()));

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() != Material.ENDER_PEARL || ItemCooldownUtil.inCooldown(player, Material.ENDER_PEARL)) {
            return;
        }
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            item = new ItemStack(Material.AIR);
        }
        player.getInventory().setItemInMainHand(item);
        player.updateInventory();
        ItemCooldownUtil.setCooldown(player, Material.ENDER_PEARL, plugin.getSuperElytraConfig().getEnderpearlUseDelay());
    }

    class ChargeTicker implements Runnable {
        @Override
        public void run() {
            Iterator<Entry<Player, Integer>> iter = chargeTime.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<Player, Integer> entry = iter.next();
                Player player = entry.getKey();

                if (!player.isOnline() || !player.isOnGround()) {
                    iter.remove();
                    continue;
                }

                int ticks = entry.getValue().intValue() + 1;
                entry.setValue(ticks);

                Location loc = player.getLocation();
                World world = player.getWorld();

                world.spawnParticle(Particle.SMOKE_NORMAL, loc, 1, 0.2F, 0.2F, 0.2F, 0.0F);
                if (ticks >= plugin.getSuperElytraConfig().getChargeUpTicks() && ticks % 20 == 0) {
                    world.spawnParticle(Particle.FLAME, loc, 1, 0.4F, 0.1F, 0.4F, 0.01F);
                    player.playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 0.1F, 0.1F);
                } else
                //
                if (ticks % 3 == 0) {
                    player.playSound(player.getLocation(), Sound.ENTITY_TNT_PRIMED, 0.1F, 0.1F);
                }
            }
        }
    }
}
