package miterenewed.mixin.entity;

import miterenewed.ModConstants;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fewer live fish: lowers the natural spawn cap of the water ambient category,
 * which holds only fish (cod, salmon, tropical fish, pufferfish).
 */
@Mixin(MobCategory.class)
public class FishSpawnCapMixin {
    @Inject(method = "getMaxInstancesPerChunk", at = @At("RETURN"), cancellable = true)
    private void mite$lowerFishCap(CallbackInfoReturnable<Integer> cir) {
        if ((Object) this == MobCategory.WATER_AMBIENT) {
            cir.setReturnValue(ModConstants.FISH_SPAWN_CAP);
        }
    }
}
