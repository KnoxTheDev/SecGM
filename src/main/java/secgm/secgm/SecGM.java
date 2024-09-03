package secgm.secgm; // Ensure this package matches your file structure

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class SecGM implements ClientModInitializer {

    private KeyBinding flyHackKey; // Keybinding for toggling the fly hack
    private boolean isFlyHackEnabled = false; // Toggle state
    private Vec3d storedPosition; // Store player's last position
    private boolean isFreecamActive = false; // Freecam-like state

    @Override
    public void onInitializeClient() {
        // Register the fly hack keybinding (F key)
        flyHackKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.secgm.flyhack", // Description
                InputUtil.Type.KEYSYM, // Key type
                GLFW.GLFW_KEY_F, // Key code for F key
                "category.secgm" // Category
        ));

        // Event listener for key presses
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (flyHackKey.wasPressed()) {
                toggleFlyHack(client);
            }

            // Handle fly mode movement if fly hack is active
            if (isFlyHackEnabled && isFreecamActive) {
                handleFlyMode(client);
            }
        });
    }

    // Toggle the fly hack on/off
    private void toggleFlyHack(MinecraftClient client) {
        try {
            isFlyHackEnabled = !isFlyHackEnabled; // Toggle state

            if (client.player != null) {
                ClientPlayerEntity player = client.player;

                if (isFlyHackEnabled) {
                    // Store the current position and enable freecam-like movement
                    storedPosition = player.getPos();
                    isFreecamActive = true;

                    // Notify user
                    client.inGameHud.setOverlayMessage(Text.of("FlyHack Enabled"), false);
                } else {
                    // Sync player position back to the stored position when toggled off
                    isFreecamActive = false;
                    sendPlayerPositionUpdate(client, storedPosition);

                    // Notify user
                    client.inGameHud.setOverlayMessage(Text.of("FlyHack Disabled"), false);
                }
            }
        } catch (Exception e) {
            // Handle exceptions and log errors
            e.printStackTrace();
            client.inGameHud.setOverlayMessage(Text.of("Error toggling FlyHack"), false);
        }
    }

    // Handles movement while fly hack is active
    private void handleFlyMode(MinecraftClient client) {
        // Movement is already handled by intercepting packets
        // Additional logic can be added if necessary
    }

    // Sync player's position back to the stored coordinates
    private void sendPlayerPositionUpdate(MinecraftClient client, Vec3d position) {
        try {
            if (client.player != null) {
                // Create a position packet with the stored position data
                PlayerMoveC2SPacket.PositionAndOnGround positionPacket = new PlayerMoveC2SPacket.PositionAndOnGround(
                        position.x,
                        position.y,
                        position.z,
                        true // On ground status
                );

                // Send the packet to the server to update the player's position
                client.getNetworkHandler().sendPacket(positionPacket);
            }
        } catch (Exception e) {
            // Handle exceptions and log errors
            e.printStackTrace();
            client.inGameHud.setOverlayMessage(Text.of("Error sending position update"), false);
        }
    }
}
