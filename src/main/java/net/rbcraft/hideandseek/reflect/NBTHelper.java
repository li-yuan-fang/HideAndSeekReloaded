package net.rbcraft.hideandseek.reflect;

import net.rbcraft.hideandseek.io.I18n;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public class NBTHelper {

    private static final String param_title = "HideAndSeek";

    public static final Class<?> NBTTagCompound = ReflectUtils.getNMSClass("nbt.NBTTagCompound");

    public static final Class<?> NMSItemStack = ReflectUtils.getNMSClass("world.item.ItemStack");

    public static final Class<?> OCBItemStack = ReflectUtils.getOCBClass("inventory.CraftItemStack");

    public static ItemStack setParam(ItemStack item, String param) {
        if (!checkEnvironment()) {
            I18n.log("&c反射获取类对象失败");
            return item;
        }
        ItemStack ret = item;
        try {
            //复制原生物品对象
            Method asNMSCopy = OCBItemStack.getMethod("asNMSCopy", ItemStack.class);
            Object itemStack = asNMSCopy.invoke(null, item);

            //获取NBT对象
            Method getTag = NMSItemStack.getMethod("u");
            Object nbt = getTag.invoke(itemStack);

            boolean noNBT = nbt == null;
            if (noNBT) nbt = NBTTagCompound.newInstance();

            //写入NBT
            Method setString = NBTTagCompound.getMethod("a", String.class, String.class);
            setString.invoke(nbt, param_title, param);

            if (noNBT) {
                Method setTag = NMSItemStack.getMethod("c", NBTTagCompound);
                setTag.invoke(itemStack, nbt);
            }

            //生成Bukkit ItemStack
            Method asBukkitCopy = OCBItemStack.getMethod("asBukkitCopy", NMSItemStack);
            ret = (ItemStack) asBukkitCopy.invoke(null, itemStack);
        } catch (Exception e) {
            I18n.log("&cNBT附加失败");
            e.printStackTrace();
        }
        return ret;
    }

    public static String getParam(ItemStack item) {
        if (!checkEnvironment()) {
            I18n.log("&c反射获取类对象失败");
            return null;
        }
        try {
            //复制原生物品对象
            Method asNMSCopy = OCBItemStack.getMethod("asNMSCopy", ItemStack.class);
            Object itemStack = asNMSCopy.invoke(null, item);

            //获取NBT对象
            Method getTag = NMSItemStack.getMethod("u");
            Object nbt = getTag.invoke(itemStack);

            if (nbt == null) return null;

            //读取参数
            Method getString = NBTTagCompound.getMethod("l", String.class);
            return (String) getString.invoke(nbt, param_title);
        } catch (Exception e) {
            I18n.log("&cNBT获取失败");
            e.printStackTrace();
        }
        return null;
    }

    private static boolean checkEnvironment() {
        return NBTTagCompound != null && NMSItemStack != null && OCBItemStack != null;
    }

}
