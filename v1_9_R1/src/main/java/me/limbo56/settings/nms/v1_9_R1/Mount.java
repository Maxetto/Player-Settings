package me.limbo56.settings.nms.v1_9_R1;

import me.limbo56.settings.version.IMount;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import net.minecraft.server.v1_9_R1.PacketPlayOutMount;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by lim_bo56
 * On 9/2/2016
 * At 5:48 PM
 */
public class Mount implements IMount {

    @Override
    public void sendMountPacket(Player player) {
        PacketPlayOutMount packet = new PacketPlayOutMount(((CraftPlayer) player).getHandle());
        EntityPlayer nmsplayer = ((CraftPlayer) player).getHandle();
        nmsplayer.playerConnection.sendPacket(packet);
    }

}
