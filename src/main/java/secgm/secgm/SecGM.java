package secgm.secgm;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

public class SecGM implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerSecGMCommand(dispatcher);
            registerVanishCommand(dispatcher);
        });
    }

    private void registerSecGMCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(net.minecraft.server.command.CommandManager.literal("secgm")
            .then(net.minecraft.server.command.CommandManager.argument("mode", IntegerArgumentType.integer(0, 3))
                .executes(context -> changeGameMode(context, IntegerArgumentType.getInteger(context, "mode")))));
    }

    private int changeGameMode(CommandContext<ServerCommandSource> context, int mode) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            GameMode gameMode = getGameModeFromInt(mode);
            if (gameMode != null) {
                player.changeGameMode(gameMode);
                player.sendMessage(Text.literal("Game mode changed discreetly.").formatted(Formatting.YELLOW), true);
            }
        }
        return 1;
    }

    private GameMode getGameModeFromInt(int mode) {
        switch (mode) {
            case 0: return GameMode.SURVIVAL;
            case 1: return GameMode.CREATIVE;
            case 2: return GameMode.ADVENTURE;
            case 3: return GameMode.SPECTATOR;
            default: return null;
        }
    }

    private void registerVanishCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(net.minecraft.server.command.CommandManager.literal("vanish")
            .executes(context -> toggleVanish(context)));
    }

    private int toggleVanish(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            boolean isVanished = player.isInvisible() && player.isInvulnerable();
            player.setInvisible(!isVanished);
            player.setInvulnerable(!isVanished);
            player.sendMessage(Text.literal((isVanished ? "Unvanished" : "Vanished") + " and made " + (isVanished ? "visible" : "invisible") + ".").formatted(Formatting.YELLOW), true);
            if (!isVanished) {
                sendFakeLeaveMessage(player);
            } else {
                sendFakeJoinMessage(player);
            }
        }
        return 1;
    }

    private void sendFakeLeaveMessage(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        world.getPlayers().forEach(p -> p.sendMessage(Text.literal(player.getDisplayName().getString() + " left the game").formatted(Formatting.YELLOW), false));
    }

    private void sendFakeJoinMessage(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        world.getPlayers().forEach(p -> p.sendMessage(Text.literal(player.getDisplayName().getString() + " joined the game").formatted(Formatting.YELLOW), false));
    }
}
