package miterenewed.handlers;

import miterenewed.Utils;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.food.FoodConstants;

/**
 * Scales the starting saturation to the player's hunger bars. Vanilla gives 5 saturation for
 * 10 bars; with 3 bars that would be a large hidden buffer, so the player gets 5 * 3/10 = 1.5.
 */
public class PlayerHungerHandler {
    public static void init() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            // No play time yet: first time this player joins this world
            if (player.getStats().getValue(Stats.CUSTOM, Stats.PLAY_TIME) == 0) {
                applyStartingSaturation(player);
            }
        });

        // Dying resets food to vanilla's starting values; returning from the End (alive) does not
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (!alive) {
                applyStartingSaturation(newPlayer);
            }
        });
    }

    private static void applyStartingSaturation(ServerPlayer player) {
        float scale = (float) Utils.getMaxFoodLevel(player) / FoodConstants.MAX_FOOD;
        player.getFoodData().setSaturation(FoodConstants.START_SATURATION * scale);
    }
}
