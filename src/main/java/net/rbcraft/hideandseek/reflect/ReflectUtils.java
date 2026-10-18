package net.rbcraft.hideandseek.reflect;

import org.bukkit.Bukkit;

public class ReflectUtils {

    public static Class<?> getNMSClass(String className) {
        String rootName = Bukkit.getServer().getClass().getName();
        try {
            return Class.forName(rootName.replace("org.bukkit.craftbukkit.v1_19_R1", "net.minecraft").
                    replace("CraftServer", className));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Class<?> getOCBClass(String className) {
        String rootName = Bukkit.getServer().getClass().getName();
        try {
            return Class.forName(rootName.replace("CraftServer", className));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}
