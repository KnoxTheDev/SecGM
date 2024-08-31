package secgm.secgm;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.network.packet.s2c.play.PlayerListItemS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListItemS2CPacket.Item;
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

            player.changeGameMode(gameMode);
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
            if (player.isInvisible()) {
                // Unvanish
                player.setInvisible(false);
                player.setInvulnerable(false); // Remove invulnerability
                showArmorAndItems(player);
                source.getServer().getPlayerManager().broadcastChatMessage(Text.of(player.getName().getString() + " joined the game"), false);
                player.sendMessage(Text.of("You are no longer vanished."), false);
            } else {
                // Vanish
                player.setInvisible(true);
                player.setInvulnerable(true); // Make player invulnerable
                hideArmorAndItems(player);
                source.getServer().getPlayerManager().broadcastChatMessage(Text.of(player.getName().getString() + " left the game"), false);
                player.sendMessage(Text.of("You are now vanished."), false);
            }
        } else {
            source.sendFeedback(() -> Text.of("This command can only be executed by a player."), false);
        }

        return 1;
    }

    private void hideArmorAndItems(ServerPlayerEntity player) {
        // Send packet to hide player's armor and items
        player.networkHandler.sendPacket(new PlayerListItemS2CPacket(
            PlayerListItemS2CPacket.Action.ADD_PLAYER,
            player.getServer().getPlayerManager().getPlayerList().stream()
                .filter(p -> !p.equals(player))
                .map(p -> new PlayerListItemS2CPacket.Entry(p.getUuid(), p.getGameMode(), p.ping, p.getDisplayName()))
                .toList()
        ));
    }

    private void showArmorAndItems(ServerPlayerEntity player) {
        // Send packet to show player's armor and items
        player.networkHandler.sendPacket(new PlayerListItemS2CPacket(
            PlayerListItemS2CPacket.Action.REMOVE_PLAYER,
            player.getServer().getPlayerManager().getPlayerList().stream()
                .filter(p -> !p.equals(player))
                .map(p -> new PlayerListItemS2CPacket.Entry(p.getUuid(), p.getGameMode(), p.ping, p.getDisplayName()))
                .toList()
        ));
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
                player.sendMessage(Text.of("Your nickname has been changed to " + name), false);
                source.sendFeedback(() -> Text.of("Successfully changed nickname to " + name), false);
            } else {
                source.sendFeedback(() -> Text.of("Invalid nickname! Use only letters, numbers, and underscores."), false);
            }
        } else {
            source.sendFeedback(() -> Text.of("This command can only be executed by a player."), false);
        }

        return 1;
    }
}
