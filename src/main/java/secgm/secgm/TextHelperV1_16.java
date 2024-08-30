package secgm.secgm;

import net.minecraft.text.LiteralText; // Use LiteralText for Minecraft 1.16
import net.minecraft.item.ItemStack;

public class TextHelperV1_16 implements TextHelper {
    @Override
    public void setCustomName(ItemStack itemStack, String name) {
        // Set custom name using LiteralText for 1.16
        itemStack.setCustomName(new LiteralText(name));
    }

    @Override
    public String getName(ItemStack itemStack) {
        // Get the name as a string
        return itemStack.getName().getString();
    }
}
