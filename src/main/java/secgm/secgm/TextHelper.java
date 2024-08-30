package secgm.secgm;

import net.minecraft.item.ItemStack;

public interface TextHelper {
    void setCustomName(ItemStack itemStack, String name);
    String getName(ItemStack itemStack);
}