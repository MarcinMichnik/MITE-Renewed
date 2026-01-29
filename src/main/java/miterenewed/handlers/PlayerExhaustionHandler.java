package miterenewed.handlers;

import miterenewed.ModConstants;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerExhaustionHandler {
    private static final Map<UUID, Boolean> WAS_ON_GROUND = new HashMap<>();
    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(PlayerExhaustionHandler::applyPassiveExhaustion);
        ServerTickEvents.END_SERVER_TICK.register(PlayerExhaustionHandler::applyExhaustionOnJump);
    }
    private static void applyPassiveExhaustion(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (!player.isCreative() && !player.isSpectator()) {
                player.getFoodData().addExhaustion(ModConstants.PASSIVE_EXHAUSTION);
            }
        }
    }

    private static void applyExhaustionOnJump(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            boolean wasOnGround = WAS_ON_GROUND.getOrDefault(player.getUUID(), true);
            boolean isOnGround = player.onGround();
            if (wasOnGround && !isOnGround && player.getDeltaMovement().y > 0.0) {
                player.getFoodData().addExhaustion(ModConstants.EXHAUSTION_ON_JUMP);
            }
            WAS_ON_GROUND.put(player.getUUID(), isOnGround);
        }
    }

}
