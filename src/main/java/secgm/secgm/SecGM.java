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
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
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

        if (source.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();

            if (player.hasStatusEffect(StatusEffects.INVISIBILITY)) {
                // Make player visible again
                player.removeStatusEffect(StatusEffects.INVISIBILITY);
                player.setInvulnerable(false);
                player.sendMessage(Text.of("You are now visible and vulnerable."), false);

                // Fake join message
                sendFakeJoinMessage(player);

                // Make player's items visible
                updateItemVisibility(player.getInventory().main, true);
                updateItemVisibility(player.getInventory().armor, true);

                // Allow mobs to target the player again
                World world = player.getWorld();
                world.getEntitiesByClass(MobEntity.class, player.getBoundingBox().expand(100), mob -> true)
                        .forEach(mob -> mob.setTarget(player));
            } else {
                // Make player invisible
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
                player.setInvulnerable(true);
                player.sendMessage(Text.of("You are now invisible and invulnerable."), false);

                // Fake leave message
                sendFakeLeaveMessage(player);

                // Make player's items invisible
                updateItemVisibility(player.getInventory().main, false);
                updateItemVisibility(player.getInventory().armor, false);

                // Prevent mobs from targeting the player
                World world = player.getWorld();
                world.getEntitiesByClass(MobEntity.class, player.getBoundingBox().expand(100), mob -> true)
                        .forEach(mob -> mob.setTarget(null));
            }
        } else {
            source.sendFeedback(() -> Text.of("This command can only be executed by a player."), false);
        }

        return 1;
    }

    private void sendFakeLeaveMessage(ServerPlayerEntity player) {
        Text leaveMessage = Text.literal(player.getName().getString() + " left the game")
                .formatted(Formatting.YELLOW);
        player.getServer().getPlayerManager().broadcast(leaveMessage, false);
    }

    private void sendFakeJoinMessage(ServerPlayerEntity player) {
        Text joinMessage = Text.literal(player.getName().getString() + " joined the game")
                .formatted(Formatting.YELLOW);
        player.getServer().getPlayerManager().broadcast(joinMessage, false);
    }

    private void updateItemVisibility(DefaultedList<ItemStack> items, boolean visible) {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                if (visible) {
                    stack.setCustomName(Text.empty()); // Clear custom name
                } else {
                    stack.setCustomName(Text.literal("Hidden Item").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
                }
            }
        }
    }
}
