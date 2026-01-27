package miterenewed.handlers;

import miterenewed.ModConstants;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

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
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (!player.isCreative() && !player.isSpectator()) {
                player.getHungerManager().addExhaustion(ModConstants.PASSIVE_EXHAUSTION);
            }
        }
    }

    private static void applyExhaustionOnJump(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            boolean wasOnGround = WAS_ON_GROUND.getOrDefault(player.getUuid(), true);
            boolean isOnGround = player.isOnGround();
            if (wasOnGround && !isOnGround && player.getVelocity().y > 0.0) {
                player.getHungerManager().addExhaustion(ModConstants.EXHAUSTION_ON_JUMP);
            }
            WAS_ON_GROUND.put(player.getUuid(), isOnGround);
        }
    }

}
