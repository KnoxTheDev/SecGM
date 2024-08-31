package secgm.secgm;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

public class SecGM {

    public static void registerCommands(CommandDispatcher<ServerCommandManager.CommandSource> dispatcher) {
        dispatcher.register(
                ServerCommandManager.literal("vanish")
                        .executes(context -> {
                            PlayerEntity player = context.getSource().getPlayer();
                            if (player != null) {
                                boolean isVanished = player.isSpectator();
                                player.setSpectator(!isVanished);
                                player.setInvisible(!isVanished);
                                player.setInvulnerable(!isVanished);

                                if (isVanished) {
                                    player.sendMessage(new LiteralText("You are no longer vanished.").setStyle(Style.EMPTY.withColor(Formatting.YELLOW)));
                                    player.getServer().getPlayerManager().broadcast(new LiteralText(player.getName().getString() + " has left the game.").setStyle(Style.EMPTY.withColor(Formatting.YELLOW)));
                                } else {
                                    player.sendMessage(new LiteralText("You are now vanished.").setStyle(Style.EMPTY.withColor(Formatting.YELLOW)));
                                    player.getServer().getPlayerManager().broadcast(new LiteralText(player.getName().getString() + " has joined the game.").setStyle(Style.EMPTY.withColor(Formatting.YELLOW)));
                                }
                            }
                            return 1;
                        })
        );

        dispatcher.register(
                ServerCommandManager.literal("secgm")
                        .requires(ServerCommandManager.permissionLevel(2)) // Adjust permission level as needed
                        .executes(context -> {
                            PlayerEntity player = context.getSource().getPlayer();
                            if (player != null) {
                                player.setGameMode(GameMode.SURVIVAL);
                                player.sendMessage(new LiteralText("Gamemode set to Survival.").setStyle(Style.EMPTY.withColor(Formatting.YELLOW)));
                            }
                            return 1;
                        })
                        .then(ServerCommandManager.literal("1")
                                .executes(context -> {
                                    PlayerEntity player = context.getSource().getPlayer();
                                    if (player != null) {
                                        player.setGameMode(GameMode.CREATIVE);
                                        player.sendMessage(new LiteralText("Gamemode set to Creative.").setStyle(Style.EMPTY.withColor(Formatting.YELLOW)));
                                    }
                                    return 1;
                                }))
                        .then(ServerCommandManager.literal("2")
                                .executes(context -> {
                                    PlayerEntity player = context.getSource().getPlayer();
                                    if (player != null) {
                                        player.setGameMode(GameMode.ADVENTURE);
                                        player.sendMessage(new LiteralText("Gamemode set to Adventure.").setStyle(Style.EMPTY.withColor(Formatting.YELLOW)));
                                    }
                                    return 1;
                                }))
                        .then(ServerCommandManager.literal("3")
                                .executes(context -> {
                                    PlayerEntity player = context.getSource().getPlayer();
                                    if (player != null) {
                                        player.setGameMode(GameMode.SPECTATOR);
                                        player.sendMessage(new LiteralText("Gamemode set to Spectator.").setStyle(Style.EMPTY.withColor(Formatting.YELLOW)));
                                    }
                                    return 1;
                                }))
        );
    }
}
