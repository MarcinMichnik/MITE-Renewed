package miterenewed.mixin.client;

import miterenewed.ModConstants;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(InGameHud.class)
public class HungerHudMixin {

    @ModifyConstant(method = "renderFood", constant = @Constant(intValue = 10))
    private int capHungerBars(int value) {
        ClientPlayerEntity player = net.minecraft.client.MinecraftClient.getInstance().player;
        if (player == null) return MathHelper.ceil(value);

        int maxFoodLevel = getMaxFoodLevel(player);
        return maxFoodLevel / 2;
    }

    private static int getMaxFoodLevel(ClientPlayerEntity player) {
        int bonus = player.experienceLevel / ModConstants.LEVELS_PER_UPGRADE;
        return (ModConstants.BASE_HUNGER + bonus) * 2;
    }

}
