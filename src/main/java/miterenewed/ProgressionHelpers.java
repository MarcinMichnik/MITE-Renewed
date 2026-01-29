package miterenewed;

import net.minecraft.server.level.ServerPlayer;

public class ProgressionHelpers {
    public static int getMaxFoodLevel(ServerPlayer player) {
        int bonus = player.experienceLevel / ModConstants.LEVELS_PER_UPGRADE;
        return (ModConstants.BASE_HUNGER + bonus) * 2;
    }

}
