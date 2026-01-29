package miterenewed.mixin.client;

import miterenewed.ModConstants;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Gui.class)
public class HungerHudMixin {

    @ModifyConstant(method = "renderFood", constant = @Constant(intValue = 10))
    private int capHungerBars(int value) {
        LocalPlayer player = net.minecraft.client.Minecraft.getInstance().player;
        if (player == null) return Mth.ceil(value);

        int maxFoodLevel = getMaxFoodLevel(player);
        return maxFoodLevel / 2;
    }

    private static int getMaxFoodLevel(LocalPlayer player) {
        int bonus = player.experienceLevel / ModConstants.LEVELS_PER_UPGRADE;
        return (ModConstants.BASE_HUNGER + bonus) * 2;
    }

}
