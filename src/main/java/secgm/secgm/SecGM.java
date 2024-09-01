package secgm.secgm;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SecGM implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("secgm")
                    .then(argument("mode", IntegerArgumentType.integer(0, 3))
                            .executes(context -> {
                                ServerPlayerEntity player = context.getSource().getPlayer();
                                int mode = IntegerArgumentType.getInteger(context, "mode");
                                GameMode gameMode = GameMode.byId(mode);
                                player.changeGameMode(gameMode);
                                context.getSource().sendFeedback(Text.literal("Gamemode changed to " + gameMode.getName()).formatted(Formatting.GREEN), true);
                                return 1;
                            })));

            dispatcher.register(literal("vanish")
                    .executes(context -> {
                        ServerPlayerEntity player = context.getSource().getPlayer();
                        boolean isVanished = player.isInvisible();
                        player.setInvisible(!isVanished);
                        player.setInvulnerable(!isVanished);
                        player.sendMessage(Text.literal("You are now " + (isVanished ? "visible" : "invisible")).formatted(isVanished ? Formatting.GREEN : Formatting.RED), true);
                        return 1;
                    }));
        });
    }
}
