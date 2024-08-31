package secgm.secgm;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import net.minecraft.world.entity.player.PlayerInventory;
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
            registersecgmCommand(dispatcher);
            registervanishCommand(dispatcher);
            registernickCommand(dispatcher);
        });
    }

    private void registersecgmCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("secgm")
                .then(CommandManager.argument("mode", IntegerArgumentType.integer(0, 3))
                        .executes(this::setGameMode)));
    }

    private void registervanishCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("vanish")
                .executes(this::vanish));
    }

    private void registernickCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("nick")
                .then(CommandManager.argument("name", StringArgumentType.string())
                        .executes(this::nick)));
    }

    private int setGameMode(CommandContext<ServerCommandSource> context) {
        int mode = IntegerArgumentType.getInteger(context, "mode");
        ServerCommandSource source = context.getSource();

        // Check if the command executor is a player
        if (source.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();
            GameMode gameMode;

            switch (mode) {
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
                    source.sendFeedback(() -> Text.of("Invalid game mode! Use 0 for Survival, 1 for Creative, 2 for Adventure, or 3 for Spectator."), false);
                    return 1;
            }

            player.setGameMode(gameMode);
            player.sendMessage(Text.of("Game mode changed to " + gameMode.getName()), false);
        } else {
            source.sendFeedback(() -> Text.of("This command can only be executed by a player."), false);
        }

        return 1;
    }

    private int vanish(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        // Check if the command executor is a player
        if (source.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();

            // Toggle vanish mode
            boolean isInvisible = !player.isInvisible();
            player.setInvisible(isInvisible);

            if (isInvisible) {
                player.sendMessage(Text.of("You are now vanished."), false);
                // Hide player's armor and items
                PlayerInventory inventory = player.getInventory();
                inventory.armorInventory.forEach(stack -> stack.setCount(stack.getCount()));
                inventory.mainInventory.forEach(stack -> stack.setCount(stack.getCount()));
            } else {
                player.sendMessage(Text.of("You are no longer vanished."), false);
                // Show player's armor and items again
                PlayerInventory inventory = player.getInventory();
                inventory.armorInventory.forEach(stack -> stack.setCount(stack.getCount()));
                inventory.mainInventory.forEach(stack -> stack.setCount(stack.getCount()));
            }
        } else {
            source.sendFeedback(() -> Text.of("This command can only be executed by a player."), false);
        }

        return 1;
    }

    private int nick(CommandContext<ServerCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        ServerCommandSource source = context.getSource();

        // Check if the command executor is a player
        if (source.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();

            // Check if the name contains only valid characters
            if (name.matches("^[a-zA-Z0-9_]+$")) {
                player.setCustomName(Text.of(name));
                player.setCustomNameVisible(true);
                player.sendMessage(Text.of("Your nickname has been changed to " + name), false);
                source.sendFeedback(() -> Text.of("Successfully changed nickname to " + name), false);
                // Update display name in chat as well
                source.getServer().getPlayerManager().broadcastChatMessage(new SignedMessage(Text.of(player.getName().getString() + " has changed their nickname to " + name)), MessageType.SYSTEM);
            } else {
                source.sendFeedback(() -> Text.of("Invalid nickname! Use only letters, numbers, and underscores."), false);
            }
        } else {
            source.sendFeedback(() -> Text.of("This command can only be executed by a player."), false);
        }

        return 1;
    }
}
