package miterenewed.mixin.player;

import miterenewed.Utils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Caps the player's food at their own maximum (Utils.getMaxFoodLevel) instead of vanilla's 20,
 * so a player with 3 hunger bars really has 6 food points.
 */
@Mixin(Player.class)
public abstract class PlayerFoodCapMixin {

    // End of tick: after eating has been applied and before the server sends the food level to the client
    @Inject(method = "tick", at = @At("RETURN"))
    private void mite$trimFoodToCap(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        FoodData foodData = player.getFoodData();
        int maxFood = Utils.getMaxFoodLevel(player);
        if (foodData.getFoodLevel() > maxFood) {
            foodData.setFoodLevel(maxFood);
        }
        // Vanilla rule: saturation never exceeds the food level
        if (foodData.getSaturationLevel() > foodData.getFoodLevel()) {
            foodData.setSaturation(foodData.getFoodLevel());
        }
    }

    // Hungry means below the player's own cap; evaluated on both sides so eating stays in sync
    @Inject(method = "canEat", at = @At("HEAD"), cancellable = true)
    private void mite$canEatBelowCap(boolean canAlwaysEat, CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) (Object) this;
        cir.setReturnValue(player.getAbilities().invulnerable || canAlwaysEat
                || player.getFoodData().getFoodLevel() < Utils.getMaxFoodLevel(player));
    }
}
