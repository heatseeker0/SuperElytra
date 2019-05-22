package com.mcspacecraft.superelytra.events;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import com.mcspacecraft.superelytra.SuperElytra;
import com.mcspacecraft.superelytra.util.PlayerXpUtils;

public class PlayerListener implements Listener {
    private final SuperElytra plugin;

    public PlayerListener(SuperElytra plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.isGliding()) {
            plugin.getPlayerManager().enhancedGlide(player);
        }
    }

    @EventHandler(ignoreCancelled = false)
    public void onEnderpearlUse(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.ENDER_PEARL) {
            return;
        }

        final Player player = event.getPlayer();
        if (!player.isGliding()) {
            return;
        }

        plugin.getPlayerManager().boostFlight(player);

        event.setCancelled(true);
    }

    @EventHandler
    public void onElytraDamage(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.ELYTRA) {
            return;
        }

        if (!item.containsEnchantment(Enchantment.MENDING)) {
            return;
        }

        final Player player = event.getPlayer();
        int playerXp = PlayerXpUtils.getPlayerExp(player);
        int cost = event.getDamage() * plugin.getSuperElytraConfig().getElytraXpPerDamage();
        if (playerXp < cost) {
            return;
        }
        PlayerXpUtils.changePlayerExp(player, -cost);

        event.setDamage(0);
        event.setCancelled(true);
    }

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (event.isSneaking()) {
            plugin.getPlayerManager().startCharging(player);
        } else {
            plugin.getPlayerManager().stopCharging(player);
        }
    }

    @EventHandler
    public void onPlayerLogon(PlayerLoginEvent event) {
        if (plugin.getSuperElytraConfig().isDefaultEnhancedFlight()) {
            plugin.getPlayerManager().enableSuperElytra(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerLogOff(PlayerQuitEvent event) {
        plugin.getPlayerManager().cancelCharging(event.getPlayer());
    }
}
