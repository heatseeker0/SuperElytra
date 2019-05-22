package com.mcspacecraft.superelytra.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ItemCooldownUtil {
    public static void setCooldown(Player player, Material item, int ticks) throws NullPointerException, IllegalArgumentException {
        if (player == null)
            throw new NullPointerException("player");
        if (item == null)
            throw new NullPointerException("item");

        Object internalItem = convertItem(item);
        if (internalItem == null)
            throw new IllegalArgumentException("item");

        invokeMethod(getSetCooldownMethod(), getItemCooldown(player), internalItem, ticks);
    }

    public static boolean inCooldown(Player player, Material item) throws NullPointerException, IllegalArgumentException {
        if (player == null)
            throw new NullPointerException("player");
        if (item == null)
            throw new NullPointerException("item");

        Object internalItem = convertItem(item);
        if (internalItem == null)
            throw new IllegalArgumentException("item");

        return (Boolean) invokeMethod(getInCooldownMethod(), getItemCooldown(player), internalItem);
    }

    /* ========== Internal ========== */

    private static Method GET_HANDLE;
    private static Method GET_ITEM_COOLDOWN;
    private static Method CONVERT_ITEM;

    private static Object getItemCooldown(Player player) {
        if (GET_HANDLE == null)
            GET_HANDLE = getMethodByName(getCraftBukkitClass("entity.CraftPlayer"), "getHandle");
        Object handle = invokeMethod(GET_HANDLE, player);

        if (GET_ITEM_COOLDOWN == null)
            GET_ITEM_COOLDOWN = getMethodByTypes(getMinecraftClass("EntityHuman"), "getItemCooldown", getItemCooldownClass());
        return invokeMethod(GET_ITEM_COOLDOWN, handle);
    }

    private static Object convertItem(Material item) {
        if (CONVERT_ITEM == null)
            CONVERT_ITEM = getMethodByName(getCraftBukkitClass("util.CraftMagicNumbers"), "getItem", Material.class);
        return invokeMethod(CONVERT_ITEM, null, item);
    }

    private static Method SET_COOLDOWN;
    private static Method IN_COOLDOWN;
    private static Class<?> ITEM_COOLDOWN_CLASS;
    private static Class<?> ITEM_CLASS;

    private static Method getSetCooldownMethod() {
        if (SET_COOLDOWN == null)
            SET_COOLDOWN = getMethodByTypes(getItemCooldownClass(), "setCooldown", void.class, getItemClass(), int.class);
        return SET_COOLDOWN;
    }

    private static Method getInCooldownMethod() {
        if (IN_COOLDOWN == null)
            IN_COOLDOWN = getMethodByTypes(getItemCooldownClass(), "inCooldown", boolean.class, getItemClass());
        return IN_COOLDOWN;
    }

    private static Class<?> getItemCooldownClass() {
        if (ITEM_COOLDOWN_CLASS == null)
            ITEM_COOLDOWN_CLASS = getMinecraftClass("ItemCooldown");
        return ITEM_COOLDOWN_CLASS;
    }

    private static Class<?> getItemClass() {
        if (ITEM_CLASS == null)
            ITEM_CLASS = getMinecraftClass("Item");
        return ITEM_CLASS;
    }

    /* ========== Reflection ========== */

    private static String OBC_PACKAGE;
    private static String NMS_PACKAGE;

    private static Class<?> getCraftBukkitClass(String name) {
        if (OBC_PACKAGE == null)
            OBC_PACKAGE = Bukkit.getServer().getClass().getPackage().getName() + ".";
        return getClass(OBC_PACKAGE + name);
    }

    private static Class<?> getMinecraftClass(String name) {
        if (OBC_PACKAGE == null)
            OBC_PACKAGE = Bukkit.getServer().getClass().getPackage().getName() + ".";
        if (NMS_PACKAGE == null)
            NMS_PACKAGE = OBC_PACKAGE.replace("org.bukkit.craftbukkit", "net.minecraft.server");
        return getClass(NMS_PACKAGE + name);
    }

    private static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to find " + name, e);
        }
    }

    private static Method getMethodByName(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find " + buildMethodString(clazz, name, null, parameterTypes));
        }
    }

    private static Method getMethodByTypes(Class<?> clazz, String name, Class<?> returnType, Class<?>... parameterTypes) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers())) {
                continue;
            }
            if (method.getReturnType().equals(returnType) && Arrays.equals(method.getParameterTypes(), parameterTypes)) {
                return method;
            }
        }
        throw new RuntimeException("Unable to find " + buildMethodString(clazz, name, returnType, parameterTypes));
    }

    private static Object invokeMethod(Method method, Object object, Object... args) {
        try {
            return method.invoke(object, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static String buildMethodString(Class<?> clazz, String name, Class<?> returnType, Class<?>... parameterTypes) {
        StringBuilder sb = new StringBuilder(clazz.getSimpleName()).append('#').append(name).append('(');
        boolean firstParameter = true;
        for (Class<?> parameterType : parameterTypes) {
            if (firstParameter) {
                firstParameter = false;
            } else {
                sb.append(", ");
            }
            sb.append(parameterType.getSimpleName());
        }
        sb.append(')');
        if (returnType != null) {
            sb.append(": ").append(returnType.getSimpleName());
        }
        return sb.toString();
    }

    private ItemCooldownUtil() {
    }
}
