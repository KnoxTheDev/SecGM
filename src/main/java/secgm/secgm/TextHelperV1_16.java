package secgm.secgm;

import net.minecraft.text.Text;
import net.minecraft.item.ItemStack;

public class TextHelperV1_16 implements TextHelper {
    @Override
    public void setCustomName(ItemStack itemStack, String name) {
        itemStack.setCustomName(Text.of(name));
    }

    @Override
    public String getName(ItemStack itemStack) {
        return itemStack.getName().getString();
    }
}