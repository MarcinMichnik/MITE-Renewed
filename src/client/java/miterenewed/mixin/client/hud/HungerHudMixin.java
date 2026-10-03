package miterenewed.mixin.client.hud;

import miterenewed.Utils;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Hud.class)
public class HungerHudMixin {

    @ModifyConstant(method = "extractFood", constant = @Constant(intValue = 10))
    private int capHungerBars(int value) {
        LocalPlayer player = net.minecraft.client.Minecraft.getInstance().player;
        if (player == null) return Mth.ceil(value);

        return Utils.getMaxFoodLevel(player) / 2;
    }

}
