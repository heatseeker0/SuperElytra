package com.mcspacecraft.superelytra.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mcspacecraft.superelytra.PlayerManager;
import com.mcspacecraft.superelytra.SuperElytra;

public class ElytraModeCommand implements CommandExecutor {
    private final SuperElytra plugin;

    public ElytraModeCommand(SuperElytra plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getSuperElytraConfig().getMessage("need-player"));
                return true;
            }

            Player player = (Player) sender;
            PlayerManager pm = plugin.getPlayerManager();
            if (pm.isEnabled(player)) {
                pm.disableSuperElytra(player);
                player.sendMessage(plugin.getSuperElytraConfig().getMessage("disabled"));
            } else {
                pm.enableSuperElytra(player);
                player.sendMessage(plugin.getSuperElytraConfig().getMessage("enabled"));
            }

            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                subCmdReload(sender);
                break;
        }

        return true;
    }

    private void subCmdReload(CommandSender sender) {
        if (sender.hasPermission("superelytra.admin")) {
            plugin.getSuperElytraConfig().load();
            sender.sendMessage(plugin.getSuperElytraConfig().getMessage("configuration-loaded"));
        }
    }
}
