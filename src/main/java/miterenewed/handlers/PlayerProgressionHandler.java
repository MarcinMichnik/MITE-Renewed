package miterenewed.handlers;

import miterenewed.ModConstants;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerProgressionHandler {
    private static final Map<UUID, Integer> LAST_LEVEL = new HashMap<>();

    public static void init() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            LAST_LEVEL.put(player.getUuid(), player.experienceLevel);
            applyProgression(player);
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            LAST_LEVEL.put(newPlayer.getUuid(), newPlayer.experienceLevel);
            applyProgression(newPlayer);
        });

        ServerTickEvents.END_SERVER_TICK.register(PlayerProgressionHandler::applyProgressionOnLevelUp);
    }

    private static void applyProgression(ServerPlayerEntity player) {
        int bonus = player.experienceLevel / ModConstants.LEVELS_PER_UPGRADE;
        int hearts = ModConstants.BASE_HEARTS + bonus;
        int hunger = ModConstants.BASE_HUNGER + bonus;

        applyHealth(player, hearts);
        applyHunger(player, hunger);
    }

    private static void applyHealth(ServerPlayerEntity player, int hearts) {
        double maxHealth = hearts * 2.0;
        EntityAttributeInstance attr = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (attr != null) {
            attr.setBaseValue(maxHealth);
        }
        if (player.getHealth() > maxHealth) {
            player.setHealth((float) maxHealth);
        }
    }

    private static void applyHunger(ServerPlayerEntity player, int hunger) {
        int maxFood = hunger * 2;
        if (player.getHungerManager().getFoodLevel() > maxFood) {
            player.getHungerManager().setFoodLevel(maxFood);
        }
        player.getHungerManager().setSaturationLevel(0.0F);
    }

    private static void applyProgressionOnLevelUp(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            int current = player.experienceLevel;
            int last = LAST_LEVEL.getOrDefault(player.getUuid(), current);

            if (current != last) {
                applyProgression(player);
                LAST_LEVEL.put(player.getUuid(), current);
            }
        }
    }

}