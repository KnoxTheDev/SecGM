package secgm.secgm;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.entity.event.v1.EntityDamageCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.util.ActionResult;

public class SecGM implements ModInitializer {

    @Override
    public void onInitialize() {
        // Register event to continuously update player abilities
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            if (player != null) {
                enableCreativeFlight(player);
            }
        });

        // Prevent fall damage
        EntityDamageCallback.EVENT.register((entity, source, amount) -> {
            if (entity instanceof ClientPlayerEntity && source == DamageSource.FALL) {
                return ActionResult.FAIL; // Cancels the fall damage
            }
            return ActionResult.PASS; // Allows other damage types
        });

        // Prevent knockback when attacked
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player != null && player instanceof ClientPlayerEntity) {
                player.setVelocity(0, 0, 0); // Stops any knockback effects
            }
            return ActionResult.PASS;
        });
    }

    // Enables Creative flight for the player
    private void enableCreativeFlight(ClientPlayerEntity player) {
        PlayerAbilities abilities = player.getAbilities();
        abilities.allowFlying = true; // Allow flying in all game modes
        abilities.flying = player.input.jumping || abilities.flying; // Start flying if jumping
        player.sendAbilitiesUpdate(); // Sync the abilities with the server
    }
}
