package me.limbo56.settings.nms.v1_8_R3;

import me.limbo56.settings.version.IItemGlower;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

/**
 * Created by lim_bo56
 * On 9/2/2016
 * At 5:45 PM
 */
public class ItemGlower implements IItemGlower {

    @Override
    public ItemStack glow(ItemStack itemStack) {
        net.minecraft.server.v1_8_R3.ItemStack localItemStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound localNBTTagCompound = null;
        if (!localItemStack.hasTag()) {
            localNBTTagCompound = new NBTTagCompound();
            localItemStack.setTag(localNBTTagCompound);
        }
        if (localNBTTagCompound == null)
            localNBTTagCompound = localItemStack.getTag();

        NBTTagList localNBTTagList = new NBTTagList();
        localNBTTagCompound.set("ench", localNBTTagList);
        localItemStack.setTag(localNBTTagCompound);
        return CraftItemStack.asCraftMirror(localItemStack);
    }

}
