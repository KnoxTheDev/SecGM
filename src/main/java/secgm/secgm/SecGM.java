package secgm.secgm;

import com.mojang.brigadier.CommandContext;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import net.minecraft.network.packet.s2c.play.PlayerListItemS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityMetadataS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListItemS2CPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Inventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecGM implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("secgm");

    @Override
    public void onInitialize() {
        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(CommandManager.literal("setgmode")
                .then(CommandManager.argument("mode", StringArgumentType.string())
                    .executes(this::setGameMode))
            );
            dispatcher.register(CommandManager.literal("vanish")
                .executes(this::vanish)
            );
            dispatcher.register(CommandManager.literal("nick")
                .then(CommandManager.argument("name", StringArgumentType.string())
                    .executes(this::nick))
            );
        });
    }

    private int setGameMode(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        String mode = StringArgumentType.getString(context, "mode");
        ServerPlayerEntity player = source.getPlayer();

        if (player != null) {
            switch (mode.toLowerCase()) {
                case "survival":
                    player.setGameMode(GameMode.SURVIVAL);
                    break;
                case "creative":
                    player.setGameMode(GameMode.CREATIVE);
                    break;
                case "adventure":
                    player.setGameMode(GameMode.ADVENTURE);
                    break;
                case "spectator":
                    player.setGameMode(GameMode.SPECTATOR);
                    break;
                default:
                    source.sendError(Text.of("Invalid game mode!"));
                    return 1;
            }
            source.sendFeedback(Text.of("Game mode set to " + mode), true);
        }
        return 1;
    }

    private int vanish(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player != null) {
            // Hide player's items and armor from other players
            hideItemsFromOthers(player);

            source.sendFeedback(Text.of("You are now invisible to others."), true);
        }
        return 1;
    }

    private int nick(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        String newName = StringArgumentType.getString(context, "name");
        ServerPlayerEntity player = source.getPlayer();

        if (player != null) {
            // Change the player's display name
            player.setCustomName(Text.of(newName));
            source.sendFeedback(Text.of("Nickname changed to " + newName), true);
        }
        return 1;
    }

    private void hideItemsFromOthers(ServerPlayerEntity player) {
        // Example logic to hide player's items and armor from other players
        // Update player's metadata to hide items (client-side)
        for (ServerPlayerEntity otherPlayer : player.getServer().getPlayerManager().getPlayerList()) {
            if (otherPlayer.equals(player)) continue;
            
            // Send an empty packet to hide items
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeVarInt(player.getId());
            buf.writeByte(0); // Packet type
            otherPlayer.networkHandler.sendPacket(new PlayerListItemS2CPacket(
                PlayerListItemS2CPacket.Action.REMOVE_PLAYER, Collections.singletonList(new GameProfile(player.getUuid(), player.getName().getString()))
            ));
        }
    }
}
