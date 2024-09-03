package secgm.secgm;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class SecGM implements ClientModInitializer {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final Input input = mc.input;

    private final KeyBinding flyHackKeyBinding = new KeyBinding(
            "key.secgm.flyhack", // Key description
            KeyBindingHelper.getOrCreate("secgm.secgm"), // Key category
            70, // Key code for F key
            "category.secgm" // Category
    );

    private boolean freecamEnabled = false;
    private double storedX, storedY, storedZ;

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(flyHackKeyBinding);
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private void onTick(MinecraftClient client) {
        if (flyHackKeyBinding.isPressed()) { // Check if the key is pressed
            if (!freecamEnabled) {
                storedX = mc.player.getX();
                storedY = mc.player.getY();
                storedZ = mc.player.getZ();
                freecamEnabled = true;
                mc.player.noClip = true;
            } else {
                freecamEnabled = false;
                mc.player.noClip = false;
                mc.player.setPos(storedX, storedY, storedZ);
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(storedX, storedY, storedZ, true));
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true)); // Send on-ground packet to disable fall damage
            }
        }

        if (freecamEnabled) {
            // Disable movement by overriding input values
            input.pressingJump = false;
            input.pressingSneak = false;
            input.forwardSpeed = 0.0F;
            input.sidewaysSpeed = 0.0F;
        }
    }
}
