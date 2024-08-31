package secgm.secgm;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.EquipmentSlot;
import net.minecraft.util.EquipmentSlot.Type;
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
        });
    }

    private void registersecgmCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("secgm")
                .then(CommandManager.argument("mode", IntegerArgumentType.integer(0, 3))
                        .executes(this::setGameMode)));
    }

    private void registervanishCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("vanish")
                .executes(this::vanishPlayer));
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
                    player.changeGameMode(GameMode.SURVIVAL);
                    break;
                case 1:
                    player.changeGameMode(GameMode.CREATIVE);
                    break;
                case 2:
                    player.changeGameMode(GameMode.ADVENTURE);
                    break;
                case 3:
                    player.changeGameMode(GameMode.SPECTATOR);
                    break;
                default:
                    source.sendFeedback(() -> Text.of("Invalid game mode! Use 0 for Survival, 1 for Creative, 2 for Adventure, or 3 for Spectator."), false);
                    return 1;
            }

            player.sendMessage(Text.of("Game mode changed to " + player.interactionManager.getGameMode().getName()), false);
        } else {
            source.sendFeedback(() -> Text.of("This command can only be executed by a player."), false);
        }

        return 1;
    }

    private int vanishPlayer(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        // Check if the command executor is a player
        if (source.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();

            // Toggle vanish mode
            player.setInvisible(!player.isInvisible());

            // Hide player from tab list
            PlayerListS2CPacket packet = new PlayerListS2CPacket(PlayerListS2CPacket.Action.REMOVE_PLAYER, player);
            player.server.getPlayerManager().sendToAll(packet);

            // Fake realistic joining and leaving messages
            if (player.isInvisible()) {
                player.server.getPlayerManager().broadcastChatMessage(Text.of(player.getName() + " left the game"), net.minecraft.network.MessageType.SYSTEM, player.getUUID());
            } else {
                player.server.getPlayerManager().broadcastChatMessage(Text.of(player.getName() + " joined the game"), net.minecraft.network.MessageType.SYSTEM, player.getUUID());
            }

            // Hide items held in both hands
            player.server.getPlayerManager().sendToAll(new EntityEquipmentUpdateS2CPacket(player.getId(), EquipmentSlot.MAINHAND, ItemStack.EMPTY));
            player.server.getPlayerManager().sendToAll(new EntityEquipmentUpdateS2CPacket(player.getId(), EquipmentSlot.OFFHAND, ItemStack.EMPTY));

            // Hide worn items
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                    player.server.getPlayerManager().sendToAll(new EntityEquipmentUpdateS2CPacket(player.getId(), slot, ItemStack.EMPTY));
                }
            }

            player.sendMessage(Text.of("Vanish mode " + (player.isInvisible() ? "enabled" : "disabled")), false);
        } else {
            source.sendFeedback(() -> Text.of("This command can only be executed by a player."), false);
        }

        return 1;
    }
}
