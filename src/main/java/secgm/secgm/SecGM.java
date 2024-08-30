package secgm.secgm;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
            registerNickCommand(dispatcher);
        });
    }

    private void registerSecgmCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("secgm")
                .then(CommandManager.argument("mode", IntegerArgumentType.integer(0, 3))
                        .executes(this::setGameMode)));
    }

    private void registerVanishCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("vanish")
                .executes(this::vanish));
    }

    private void registerNickCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
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
                    source.sendFeedback(() -> Text.literal("Invalid game mode! Use 0 for Survival, 1 for Creative, 2 for Adventure, or 3 for Spectator."), false);
                    return 1;
            }

            player.changeGameMode(gameMode);
            player.sendMessage(Text.literal("Game mode changed to " + gameMode.getName()), false);
        } else {
            source.sendFeedback(() -> Text.literal("This command can only be executed by a player."), false);
        }

        return 1;
    }

    private int vanish(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        // Check if the command executor is a player
        if (source.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();

            // Toggle vanish mode
            if (player.isInvisible()) {
                // Unvanish
                player.setInvisible(false);
                player.getInventory().armor.forEach(itemStack -> {
                    if (!itemStack.isEmpty()) {
                        itemStack.setCustomName(Text.literal(itemStack.getName().getString()));
                    }
                }); // Show worn armor
                player.getInventory().main.forEach(itemStack -> {
                    if (!itemStack.isEmpty()) {
                        itemStack.setCustomName(Text.literal(itemStack.getName().getString()));
                    }
                }); // Show held items
                player.sendMessage(Text.literal("You are no longer vanished.").formatted(Formatting.YELLOW), false);
            } else {
                // Vanish
                player.setInvisible(true);
                player.getInventory().armor.forEach(itemStack -> {
                    if (!itemStack.isEmpty()) {
                        itemStack.setCustomName(Text.literal("Hidden Armor"));
                    }
                }); // Hide worn armor
                player.getInventory().main.forEach(itemStack -> {
                    if (!itemStack.isEmpty()) {
                        itemStack.setCustomName(Text.literal("Hidden Item"));
                    }
                }); // Hide held items
                player.sendMessage(Text.literal("You are now vanished.").formatted(Formatting.YELLOW), false);
            }
        } else {
            source.sendFeedback(() -> Text.literal("This command can only be executed by a player."), false);
        }

        return 1;
    }

    private int nick(CommandContext<ServerCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        ServerCommandSource source = context.getSource();

        // Check if the command executor is a player
        if (source.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();
            player.setCustomName(Text.literal(name));
            player.sendMessage(Text.literal("Your nickname has been set to " + name).formatted(Formatting.GREEN), false);
        } else {
            source.sendFeedback(() -> Text.literal("This command can only be executed by a player."), false);
        }

        return 1;
    }
}
