package me.limbo56.settings.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lim_bo56
 * On Aug 7, 2016
 * At 2:24:15 AM
 */
public class ItemFactory {

    /**
     * Method used to create an itemstack
     *
     * @param displayName Name of the item
     * @param lore        The lore of the item
     * @param useLore     Boolean for lore
     * @param material    The Material
     * @param amount      Amount of items
     * @param data        Data byte
     * @return ItemStack
     */
    public static ItemStack createItem(String displayName, boolean useLore, Material material, int amount, int data, String... lore) {
        org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(material, amount, (byte) data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);

        if (useLore) {
            List<String> finalLore = meta.hasLore() ? meta.getLore() : new ArrayList<String>();
            for (String s : lore)
                if (s != null)
                    finalLore.add(s.replace("&", "§"));
            meta.setLore(finalLore);
        }

        item.setItemMeta(meta);
        return item;
    }

}
