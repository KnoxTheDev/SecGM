package secgm.secgm;

// Import statements should match the correct version. Adjust based on your workspace setup.
import net.minecraft.text.Text; // Correct import for text handling in Minecraft 1.17
import net.minecraft.item.ItemStack; // Correct import for ItemStack in Minecraft 1.17

public class TextHelperV1_17 implements TextHelper {
    @Override
    public void setCustomName(ItemStack itemStack, String name) {
        // Using Text.literal for newer Minecraft versions (1.17+)
        itemStack.setCustomName(Text.literal(name));
    }

    @Override
    public String getName(ItemStack itemStack) {
        // Retrieve the item's name as a string
        return itemStack.getName().getString();
    }
}