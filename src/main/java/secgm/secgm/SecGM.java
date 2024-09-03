package secgm.secgm;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.MovementInput;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class SecGM implements ClientModInitializer {
    private MinecraftClient mc = MinecraftClient.getInstance();
    private MovementInput movementInput;

    private boolean freecamEnabled = false;
    private double storedX, storedY, storedZ;

    @Override
    public void onInitializeClient() {
        movementInput = mc.getInput().getMovementInput();
        KeyBindingHelper.registerKeyBinding(new net.fabricmc.fabric.api.client.keybinding.v1.KeyBinding("FlyHack", net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.getOrCreate("secgm.secgm"), 70)); // F key
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private void onTick() {
        if (mc.getInput().isKeyPressed(70)) { // F key
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
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2
