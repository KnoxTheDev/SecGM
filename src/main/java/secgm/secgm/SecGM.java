package secgm.secgm;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.MovementInput;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class SecGM implements ClientModInitializer {
    private MinecraftClient mc = MinecraftClient.getInstance();
    private KeyBinding flyHackKey;

    private boolean freecamEnabled = false;
    private double storedX, storedY, storedZ;

    @Override
    public void onInitializeClient() {
        flyHackKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("FlyHack", InputUtil.Type.KEYSYM, 70, "secgm.secgm")); // F key
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private void onTick() {
        if (flyHackKey.wasPressed()) {
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
            mc.player.input = new MovementInput();
            mc.player.input.jump = false;
            mc.player.input.sneak = false;
            mc.player.input.moveForward = 0;
            mc.player.input.moveStrafe = 0;
        }
    }
}
