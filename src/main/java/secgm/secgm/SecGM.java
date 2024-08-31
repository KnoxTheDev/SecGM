package secgm.secgm;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecGM implements ModInitializer {
    public static final String MOD_ID = "secgm";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Hello Fabric world!");
        registerCommands();
    }

    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerSecgmCommand(dispatcher);
            registerVanishCommand(dispatcher);
        });
    }

    private void registerSecgmCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("secgm")
                .then(CommandManager.argument("gameModeId", IntegerArgumentType.integer(0, 3))
                        .executes(this::executeSecgmCommand)));
    }

    private void registerVanishCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("vanish")
                .executes(this::executeVanishCommand));
    }

    private int executeSecgmCommand(CommandContext<ServerCommandSource> context) {
        int gameModeId = IntegerArgumentType.getInteger(context, "gameModeId");
        ServerCommandSource source = context.getSource();

        // Check if the command executor is a player
        if (source.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();
            GameMode gameMode;

            switch (gameModeId) {
                case 0:
                    gameMode = GameMode.SURVIVAL;
                    break;
                case 1:
                    gameMode = GameMode.CREATIVE;
                    break;
                case 2:
                    gameMode = GameMode.ADVENTURE;
                    break;
                case 3:
                    gameMode = GameMode.SPECTATOR;
                    break;
                default:
                    source.sendFeedback(() -> Text.of("Invalid game mode ID."), false);
                    return 0;
            }

            player.changeGameMode(gameMode);
            player.sendMessage(Text.of("Game mode changed to " + gameMode.getName()), false);
        } else {
            source.sendFeedback(() -> Text.of("This command can only be executed by a player."), false);
        }

        return 1;
    }

    private int executeVanishCommand(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        // Check if the command executor is a player
        if (source.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();

            // Toggle vanish status
            if (player.hasStatusEffect(StatusEffects.INVISIBILITY)) {
                player.removeStatusEffect(StatusEffects.INVISIBILITY);
                player.setInvulnerable(false);
                player.sendMessage(Text.of("You are now visible and vulnerable."), false);

                // Make player's items and armor visible
                player.getInventory().forEach(stack -> {
                    if (stack != null) {
                        stack.setGlowingTag(stack.getOrCreateTag());
                    }
                });
                player.getArmorItems().forEach(stack -> {
                    if (stack != null) {
                        stack.setGlowingTag(stack.getOrCreateTag());
                    }
                });

                // Allow mobs to target the player again
                player.getWorld().getEntities().forEach(entity -> {
                    if (entity instanceof MobEntity) {
                        ((MobEntity) entity).setTarget(player);
                    }
                });
            } else {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
                player.setInvulnerable(true);
                player.sendMessage(Text.of("You are now invisible and invulnerable."), false);

                // Make player's items and armor invisible
                player.getInventory().forEach(stack -> {
                    if (stack != null) {
                        stack.setGlowingTag(null);
                    }
                });
                player.getArmorItems().forEach(stack -> {
                    if (stack != null) {
                        stack.setGlowingTag(null);
                    }
                });

                // Prevent mobs from targeting the player
                player.getWorld().getEntities().forEach(entity -> {
                    if (entity instanceof MobEntity) {
                        ((MobEntity) entity).setTarget(null);
                    }
                });
            }
        } else {
            source.sendFeedback(() -> Text.of("This command can only be executed by a player."), false);
        }

        return 1;
    }
        }
