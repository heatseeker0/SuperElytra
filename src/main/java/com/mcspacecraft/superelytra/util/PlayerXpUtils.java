package com.mcspacecraft.superelytra.util;

import org.bukkit.entity.Player;

/**
 * Player experience manipulation utilities. Pulled from Essentials.
 * 
 * @author Catalin Ionescu
 *
 */
public class PlayerXpUtils {
    /**
     * Returns amount of EXP needed to reach a certain level.
     * 
     * @param level Level to reach
     * @return XP needed
     */
    public static int getExpToLevelUp(int level) {
        if (level <= 15) {
            return 2 * level + 7;
        } else if (level <= 30) {
            return 5 * level - 38;
        } else {
            return 9 * level - 158;
        }
    }

    /**
     * Returns total experience up to a level.
     * 
     * @param level Level
     * @return Total XP at that level
     */
    public static int getExpAtLevel(int level) {
        if (level <= 16) {
            return (int) (Math.pow(level, 2) + 6 * level);
        } else if (level <= 31) {
            return (int) (2.5 * Math.pow(level, 2) - 40.5 * level + 360.0);
        } else {
            return (int) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220.0);
        }
    }

    /**
     * Returns player's current EXP amount.
     * 
     * @param player Player to get XP
     * @return Player current XP
     */
    public static int getPlayerExp(Player player) {
        int exp = 0;
        int level = player.getLevel();

        // Get the amount of XP in past levels
        exp += getExpAtLevel(level);

        // Get amount of XP towards next level
        exp += Math.round(getExpToLevelUp(level) * player.getExp());

        return exp;
    }

    /**
     * Give or take EXP from a player.
     * 
     * @param player Player to work on
     * @param exp Experience to change by. Positive to add, negative to subtract from current player experience.
     * @return New XP amount the player was set at
     */
    public static int changePlayerExp(Player player, int exp) {
        // Get player's current exp
        int currentExp = getPlayerExp(player);

        // Reset player's current exp to 0
        player.setExp(0);
        player.setLevel(0);

        // Give the player their exp back, with the difference
        int newExp = currentExp + exp;
        player.giveExp(newExp);

        // Return the player's new exp amount
        return newExp;
    }
}
