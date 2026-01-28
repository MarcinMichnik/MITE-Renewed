package miterenewed;

import net.minecraft.server.network.ServerPlayerEntity;

public class ProgressionHelpers {
    public static int getMaxFoodLevel(ServerPlayerEntity player) {
        int bonus = player.experienceLevel / ModConstants.LEVELS_PER_UPGRADE;
        return (ModConstants.BASE_HUNGER + bonus) * 2;
    }

}
