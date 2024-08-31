package com.secgm.secgm;

import net.minecraft.server.command.Command;
import net.minecraft.server.command.CommandRegistry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class SecGM implements ModInitializer {

    public static final String MOD_ID = "secgm";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    private static boolean isVanished = false;

    @Override
    public void onInitialize() {
        LOGGER.info("SecGM mod initialized!");

        // Register the /secgm command
        try {
            CommandRegistry.registerCommand("secgm", new SecGMCommand());
            CommandRegistry.registerCommand("vanish", new VanishCommand());
        } catch (Exception e) {
            LOGGER.error("Error registering commands:", e);
        }
    }

    public static class SecGMCommand implements Command {
        @Override
        public int execute(ServerCommandSource source, String[] args) {
            if (!(source.getEntity() instanceof ServerPlayerEntity)) {
                source.sendError(new LiteralText("Only players can use this command!"));
                return 1;
            }

            try {
                ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();

                if (args.length == 0) {
                    source.sendError(new LiteralText("Usage: /secgm <0|1|2|3>"));
                    return 1;
                }

                int gamemode;
                try {
                    gamemode = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    source.sendError(new LiteralText("Invalid gamemode!"));
                    return 1;
                }

                if (gamemode < 0 || gamemode > 3) {
                    source.sendError(new LiteralText("Invalid gamemode!"));
                    return 1;
                }

                // Change gamemode discreetly
                player.setGameMode(gamemode);
                source.sendFeedback(new LiteralText("Gamemode changed to " + gamemode).formatted(Formatting.GREEN), false);
                return 0;
            } catch (Exception e) {
                LOGGER.error("Error executing /secgm command:", e);
                source.sendError(new LiteralText("An error occurred while executing the command!"));
                return 1;
            }
        }
    }

    public static class VanishCommand implements Command {
        @Override
        public int execute(ServerCommandSource source, String[] args) {
            if (!(source.getEntity() instanceof ServerPlayerEntity)) {
                source.sendError(new LiteralText("Only players can use this command!"));
                return 1;
            }

            try {
                ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();

                if (isVanished) {
                    unVanish(player);
                    isVanished = false;
                } else {
                    vanish(player);
                    isVanished = true;
                }

                return 0;
            } catch (Exception e) {
                LOGGER.error("Error executing /vanish command:", e);
                source.sendError(new LiteralText("An error occurred while executing the command!"));
                return 1;
            }
        }

        private void vanish(ServerPlayerEntity player) {
            try {
                // Fake leave message with yellow formatting
                Text leaveMessage = new LiteralText(player.getEntityName() + " left the game.").setStyle(Style.EMPTY.withColor(TextColor.YELLOW));
                player.getServer().getPlayerManager().broadcastChatMessage(leaveMessage, false);

                // Hide the player from the server
                player.setInvisible(true);
                player.setInvulnerable(true);
                player.setSilent(true);
            } catch (Exception e) {
                LOGGER.error("Error vanishing player:", e);
            }
        }

        private void unVanish(ServerPlayerEntity player) {
            try {
                // Fake join message with yellow formatting
                Text joinMessage = new LiteralText(player.getEntityName() + " joined the game.").setStyle(Style.EMPTY.withColor(TextColor.YELLOW));
                player.getServer().getPlayerManager().broadcastChatMessage(joinMessage, false);

                // Unhide the player from the server
                player.setInvisible(false);
                player.setInvulnerable(false);
                player.setSilent(false);
            } catch (Exception e) {
                LOGGER.error("Error unvanishing player:", e);
            }
        }
    }
}
