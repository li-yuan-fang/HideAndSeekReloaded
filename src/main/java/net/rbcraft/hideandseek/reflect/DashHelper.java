package net.rbcraft.hideandseek.reflect;

import net.rbcraft.hideandseek.io.I18n;
import org.bukkit.entity.Player;

public class DashHelper {

    private static final Class<?> EntityPlayer = ReflectUtils.getNMSClass("server.level.EntityPlayer");

    private static final Class<?> PlayerConnection = ReflectUtils.getNMSClass("server.network.PlayerConnection");

    private static final Class<?> PacketPlayOutEntityVelocity = ReflectUtils.getNMSClass(
            "network.protocol.game.PacketPlayOutEntityVelocity");

    private static final Class<?> Vec3D = ReflectUtils.getNMSClass("world.phys.Vec3D");

    private static final Class<?> Packet = ReflectUtils.getNMSClass("network.protocol.Packet");

    private static final Class<?> CraftPlayer = ReflectUtils.getOCBClass("entity.CraftPlayer");

    public static void dash(Player player, double x, double y, double z) {
        if (!checkEnvironment()) {
            I18n.log("&c反射获取类对象失败");
            return;
        }

        try {
            //获取原生EntityPlayer对象
            Object entityPlayer = CraftPlayer.getMethod("getHandle").invoke(player);

            //获取PlayerConnection
            Object connection = EntityPlayer.getField("b").get(entityPlayer);

            //构建包
            Object vec = Vec3D.getConstructor(double.class, double.class, double.class)
                    .newInstance(x * 2, y * 2, z * 2);
            Object packet = PacketPlayOutEntityVelocity
                    .getConstructor(int.class, Vec3D)
                    .newInstance(player.getEntityId(), vec);

            //发送包
            PlayerConnection.getMethod("a", Packet).invoke(connection, packet);
        } catch (Exception e) {
            I18n.log("&c发送冲刺消息失败");
            e.printStackTrace();
        }
    }

    private static boolean checkEnvironment() {
        return EntityPlayer != null && PlayerConnection != null && PacketPlayOutEntityVelocity != null &&
                Packet != null && CraftPlayer != null;
    }

}
