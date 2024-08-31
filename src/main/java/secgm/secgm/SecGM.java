package secgm.secgm;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;

public class SecGM implements ClientModInitializer {

    public static final String MOD_ID = "secgm";
    public static final String VANISH_PACKET_ID = MOD_ID + ":vanish_toggle";

    private static final Map<GameProfile, Boolean> vanishedPlayers = new HashMap<>();

    @Override
    public void onInitializeClient() {
        // Register Client Command using ClientCommandManager
        ClientCommandManager.DISPATCHER.register(
                ClientCommandManager.builder("vanish")
                        .executes(context -> {
                            MinecraftClient client = MinecraftClient.getInstance();
                            if (client.player != null) {
                                toggleVanish(client.player);
                            }
                            return 1; // Command execution successful
                        })
                        .build()
        );

        // Register Packet Handlers (Client and Server)
        ClientPlayNetworking.registerReceiver(VANISH_PACKET_ID, (client, context, data, sender) -> {
            PacketByteBuf buf = data.asByteBuf();
            GameProfile profile = buf.readGameProfile();
            vanishedPlayers.put(profile, buf.readBoolean());
            client.execute(() -> {
                PlayerEntity player = client.world.getPlayerByUuid(profile.getId());
                if (player != null) {
                    updatePlayerVisibility(player);
                }
            });
        });

        ServerPlayNetworking.registerReceiver(VANISH_PACKET_ID, (server, player, context, data, sender) -> {
            PacketByteBuf buf = data.asByteBuf();
            GameProfile profile = buf.readGameProfile();
            boolean isVanished = buf.readBoolean();
            vanishedPlayers.put(profile, isVanished);
            server.execute(() -> {
                ServerPlayerEntity serverPlayer = server.getPlayerByUuid(profile.getId());
                if (serverPlayer != null) {
                    toggleVanish(serverPlayer);
                    sendFakeJoinLeaveMessages(serverPlayer, isVanished);
                }
            });
        });
    }

    private void toggleVanish(PlayerEntity player) {
        boolean isVanished = !vanishedPlayers.getOrDefault(player.getGameProfile(), false);
        vanishedPlayers.put(player.getGameProfile(), isVanished);

        player.setInvisible(isVanished);
        player.setInvulnerable(isVanished);
        updatePlayerVisibility(player);
    }

    private void updatePlayerVisibility(PlayerEntity player) {
        MinecraftServer server = player.getServer();
        if (server != null) {
            for (ServerPlayerEntity otherPlayer : server.getPlayerList()) {
                if (otherPlayer != player) {
                    otherPlayer.networkHandler.sendPacket(createVanishPacket(player.getGameProfile(), vanishedPlayers.get(player.getGameProfile())));
                }
            }
        }
    }

    private void sendFakeJoinLeaveMessages(ServerPlayerEntity player, boolean isVanished) {
        MinecraftServer server = player.getServer();
        if (server != null) {
            Text joinLeaveMessage;
            if (isVanished) {
                joinLeaveMessage = Text.of(player.getDisplayName().getString() + " has left the game.");
            } else {
                joinLeaveMessage = Text.of(player.getDisplayName().getString() + " has joined the game.");
            }
            server.getPlayerList().broadcast(joinLeaveMessage);
        }
    }

    private PacketByteBuf createVanishPacket(GameProfile profile, boolean isVanished) {
        PacketByteBuf buf = new PacketByteBuf(new Buffer());
        buf.writeGameProfile(profile);
        buf.writeBoolean(isVanished);
        return buf;
    }
}
