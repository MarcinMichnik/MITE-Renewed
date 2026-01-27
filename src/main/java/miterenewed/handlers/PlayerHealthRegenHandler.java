package miterenewed.handlers;

import miterenewed.ModConstants;
import miterenewed.ProgressionHelpers;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerHealthRegenHandler {
    private static int regenTickCounter = 0;
    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(PlayerHealthRegenHandler::applyPassiveHealthRegen);
    }
    private static void applyPassiveHealthRegen(MinecraftServer server) {
        regenTickCounter++;
        if (regenTickCounter < ModConstants.REGEN_INTERVAL_TICKS) return;
        regenTickCounter = 0;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (player.isDead()) continue;
            if (player.getHealth() >= player.getMaxHealth()) continue;

            int maxFood = ProgressionHelpers.getMaxFoodLevel(player);
            int food = player.getHungerManager().getFoodLevel();

            if (food >= maxFood) {
                player.heal(ModConstants.REGEN_AMOUNT);
                player.getHungerManager().addExhaustion(0.5F);
            }
        }
    }

}
