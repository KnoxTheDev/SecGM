package secgm.secgm;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBinding;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class SecGM implements ClientModInitializer {
    private MinecraftClient mc = MinecraftClient.getInstance();
    private Input input;

    private boolean freecamEnabled = false;
    private double storedX, storedY, storedZ;

    @Override
    public void onInitializeClient() {
        input = mc.getInput();
        KeyBinding flyHackKeyBinding = new KeyBinding("FlyHack", KeyBindingHelper.getOrCreate("secgm.secgm"), 70); // F key
        KeyBindingHelper.registerKeyBinding(flyHackKeyBinding);
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private void onTick() {
        if (input.isKeyPressed(flyHackKeyBinding.getDefaultKey())) { // F key
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
            input.jump = false;
            input.sneak = false;
            input.moveForward = 0;
            input.moveStrafe = 0;
        }
    }
}
