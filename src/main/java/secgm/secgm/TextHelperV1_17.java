package secgm.secgm;

import net.minecraft.network.chat.Component; // Use Component for Minecraft 1.17+
import net.minecraft.world.item.ItemStack;

public class TextHelperV1_17 implements TextHelper {
    @Override
    public void setCustomName(ItemStack itemStack, String name) {
        // Set custom name using Component.literal for 1.17+
        itemStack.setHoverName(Component.literal(name));
    }

    @Override
    public String getName(ItemStack itemStack) {
        // Get the name as a string
        return itemStack.getHoverName().getString();
    }
}
