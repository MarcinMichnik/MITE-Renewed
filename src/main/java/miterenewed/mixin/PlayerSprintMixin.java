package miterenewed.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerSprintMixin {
    @Inject(method = "hasEnoughFoodToDoExhaustiveManoeuvres()Z", at = @At("HEAD"), cancellable = true)
    private void allowSprintLowHunger(CallbackInfoReturnable<Boolean> cir) {
        // allow sprint if foodLevel > 1
        Player player = (Player)(Object)this;
        if (player.getFoodData().getFoodLevel() > 1) {
            cir.setReturnValue(true);
        }
    }

}
