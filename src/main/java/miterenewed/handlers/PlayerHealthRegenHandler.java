package miterenewed.handlers;

import miterenewed.ModConstants;
import miterenewed.ProgressionHelpers;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class PlayerHealthRegenHandler {
    private static int regenTickCounter = 0;
    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(PlayerHealthRegenHandler::applyPassiveHealthRegen);
    }
    private static void applyPassiveHealthRegen(MinecraftServer server) {
        regenTickCounter++;
        if (regenTickCounter < ModConstants.REGEN_INTERVAL_TICKS) return;
        regenTickCounter = 0;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.isDeadOrDying()) continue;
            if (player.getHealth() >= player.getMaxHealth()) continue;

            int maxFood = ProgressionHelpers.getMaxFoodLevel(player);
            int food = player.getFoodData().getFoodLevel();

            if (food >= maxFood) {
                player.heal(ModConstants.REGEN_AMOUNT);
                player.getFoodData().addExhaustion(0.5F);
            }
        }
    }

}
