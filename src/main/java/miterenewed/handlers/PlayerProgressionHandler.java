package miterenewed.handlers;

import miterenewed.ModConstants;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerProgressionHandler {
    private static final Map<UUID, Integer> LAST_LEVEL = new HashMap<>();

    public static void init() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            LAST_LEVEL.put(player.getUUID(), player.experienceLevel);
            applyProgression(player);
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            LAST_LEVEL.put(newPlayer.getUUID(), newPlayer.experienceLevel);
            applyProgression(newPlayer);
        });

        ServerTickEvents.END_SERVER_TICK.register(PlayerProgressionHandler::applyProgressionOnLevelUp);
    }

    private static void applyProgression(ServerPlayer player) {
        int bonus = player.experienceLevel / ModConstants.LEVELS_PER_UPGRADE;
        int hearts = ModConstants.BASE_HEARTS + bonus;
        int hunger = ModConstants.BASE_HUNGER + bonus;

        applyHealth(player, Math.min(hearts, 10));
        applyHunger(player, Math.min(hunger, 10));
    }

    private static void applyHealth(ServerPlayer player, int hearts) {
        double maxHealth = hearts * 2.0;
        AttributeInstance attr = player.getAttribute(Attributes.MAX_HEALTH);
        if (attr != null) {
            attr.setBaseValue(maxHealth);
        }
        if (player.getHealth() > maxHealth) {
            player.setHealth((float) maxHealth);
        }
    }

    private static void applyHunger(ServerPlayer player, int hunger) {
        int maxFood = hunger * 2;
        if (player.getFoodData().getFoodLevel() > maxFood) {
            player.getFoodData().setFoodLevel(maxFood);
        }
        player.getFoodData().setSaturation(0.0F);
    }

    private static void applyProgressionOnLevelUp(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            int current = player.experienceLevel;
            int last = LAST_LEVEL.getOrDefault(player.getUUID(), current);

            if (current != last) {
                applyProgression(player);
                LAST_LEVEL.put(player.getUUID(), current);
            }
        }
    }

}