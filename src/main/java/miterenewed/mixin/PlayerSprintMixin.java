package miterenewed.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerSprintMixin {
    @Inject(method = "canSprintOrFly()Z", at = @At("HEAD"), cancellable = true)
    private void allowSprintLowHunger(CallbackInfoReturnable<Boolean> cir) {
        // allow sprint if foodLevel > 1
        PlayerEntity player = (PlayerEntity)(Object)this;
        if (player.getHungerManager().getFoodLevel() > 1) {
            cir.setReturnValue(true);
        }
    }

}
