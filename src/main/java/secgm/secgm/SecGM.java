package secgm.secgm;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SecGM implements ClientModInitializer {

    public static final String MOD_ID = "secgm";
    public static final Identifier VANISH_PACKET_ID = new Identifier(MOD_ID, "vanish_toggle");

    private static final Map<UUID, Boolean> vanishedPlayers = new HashMap<>();

    @Override
    public void onInitializeClient() {
        // Register Client Command using ClientCommandManager
        ClientCommandManager.INSTANCE.getDispatcher().register(
                ClientCommandManager.literal("vanish")
                        .executes(context -> {
                            MinecraftClient client = MinecraftClient.getInstance();
                            if (client.player != null) {
                                toggleVanish(client.player);
                            }
                            return 1; // Command execution successful
                        })
        );

        // Register Packet Handlers (Client and Server)
        ClientPlayNetworking.registerGlobalReceiver(VANISH_PACKET_ID, (client, handler, buf, responseSender) -> {
            UUID profileId = buf.readUuid();
            boolean isVanished = buf.readBoolean();
            client.execute(() -> {
                PlayerEntity player = client.world.getPlayerByUuid(profileId);
                if (player != null) {
                    vanishedPlayers.put(profileId, isVanished);
                    updatePlayerVisibility(player);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(VANISH_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            UUID profileId = buf.readUuid();
            boolean isVanished = buf.readBoolean();
            server.execute(() -> {
                ServerPlayerEntity serverPlayer = server.getPlayerManager().getPlayer(profileId);
                if (serverPlayer != null) {
                    vanishedPlayers.put(profileId, isVanished);
                    toggleVanish(serverPlayer);
                    sendFakeJoinLeaveMessages(serverPlayer, isVanished);
                }
            });
        });
    }

    private void toggleVanish(PlayerEntity player) {
        UUID profileId = player.getUuid();
        boolean isVanished = !vanishedPlayers.getOrDefault(profileId, false);
        vanishedPlayers.put(profileId, isVanished);

        player.setInvisible(isVanished);
        player.setInvulnerable(isVanished);
        updatePlayerVisibility(player);
    }

    private void updatePlayerVisibility(PlayerEntity player) {
        MinecraftServer server = player.getServer();
        if (server != null) {
            for (ServerPlayerEntity otherPlayer : server.getPlayerManager().getPlayerList()) {
                if (otherPlayer != player) {
                    otherPlayer.networkHandler.sendPacket(createVanishPacket(player.getUuid(), vanishedPlayers.get(player.getUuid())));
                }
            }
        }
    }

    private void sendFakeJoinLeaveMessages(ServerPlayerEntity player, boolean isVanished) {
        MinecraftServer server = player.getServer();
        if (server != null) {
            Text joinLeaveMessage = Text.of(player.getDisplayName().getString() + (isVanished ? " has left the game." : " has joined the game."));
            server.getPlayerManager().broadcast(joinLeaveMessage, false);
        }
    }

    private PacketByteBuf createVanishPacket(UUID profileId, boolean isVanished) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeUuid(profileId);
        buf.writeBoolean(isVanished);
        return buf;
    }
}
