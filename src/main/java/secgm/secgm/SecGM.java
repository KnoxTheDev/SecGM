package secgm.secgm;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SecGM implements ModInitializer {

    private static final String VANISH_STATUS_FILE = "vanish_status.json";
    private Map<UUID, Boolean> vanishStatuses = new HashMap<>();
    private Gson gson = new Gson();

    @Override
    public void onInitialize() {
        loadVanishStatuses();
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
            UUID playerId = player.getUuid();
            boolean isVanished = vanishStatuses.getOrDefault(playerId, false);
            vanishStatuses.put(playerId, !isVanished);
            updatePlayerVisibility(player, !isVanished);
            saveVanishStatuses();
            player.sendMessage(Text.literal((isVanished ? "Unvanished" : "Vanished") + " and made " + (isVanished ? "visible" : "invisible") + ".").formatted(Formatting.YELLOW), true);
            if (!isVanished) {
                sendFakeLeaveMessage(player);
            } else {
                sendFakeJoinMessage(player);
            }
        }
        return 1;
    }

    private void updatePlayerVisibility(ServerPlayerEntity player, boolean isVisible) {
        player.setInvisible(!isVisible);
        player.setInvulnerable(!isVisible);

        // Update the visibility of items and armor
        for (int slot = 0; slot < player.getInventory().size(); slot++) {
            ItemStack item = player.getInventory().getStack(slot);
            if (!item.isEmpty()) {
                NbtCompound nbt = item.getOrCreateNbt();
                nbt.putBoolean("Invisible", !isVisible);
                item.setNbt(nbt);
            }
        }
    }

    private void sendFakeLeaveMessage(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        world.getPlayers().forEach(p -> p.sendMessage(Text.literal(player.getDisplayName().getString() + " left the game").formatted(Formatting.YELLOW), false));
    }

    private void sendFakeJoinMessage(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        world.getPlayers().forEach(p -> p.sendMessage(Text.literal(player.getDisplayName().getString() + " joined the game").formatted(Formatting.YELLOW), false));
    }

    private void loadVanishStatuses() {
        File file = new File(VANISH_STATUS_FILE);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type type = new TypeToken<Map<UUID, Boolean>>(){}.getType();
                vanishStatuses = gson.fromJson(reader, type);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveVanishStatuses() {
        File file = new File(VANISH_STATUS_FILE);
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(vanishStatuses, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
