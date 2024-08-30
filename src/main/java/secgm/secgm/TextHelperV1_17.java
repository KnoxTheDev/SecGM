package secgm.secgm;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Text;
import net.minecraft.world.item.ItemStack;

public class TextHelperV1_17 implements TextHelper {
    @Override
    public void setCustomName(ItemStack itemStack, String name) {
        itemStack.setCustomName(Text.literal(name));
    }

    @Override
    public String getName(ItemStack itemStack) {
        return itemStack.getName().getString();
    }
}