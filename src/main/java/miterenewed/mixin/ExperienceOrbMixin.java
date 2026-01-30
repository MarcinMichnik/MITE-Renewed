package miterenewed.mixin;

import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExperienceOrb.class)
public class ExperienceOrbMixin {

    @Inject(method = "getValue", at = @At("RETURN"), cancellable = true)
    private void halveExperienceValue(CallbackInfoReturnable<Integer> cir) {
        int originalValue = cir.getReturnValue();
        int modifiedExpValue = originalValue > 0 ? Math.max(1, originalValue / 2) : 0;
        cir.setReturnValue(modifiedExpValue);
    }
}