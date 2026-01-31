package miterenewed.mixin;


import miterenewed.ModConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CropBlock.class)
public class SlowCropGrowthMixin {

    @Inject(method = "getGrowthSpeed", at = @At("RETURN"), cancellable = true)
    private static void reduceGrowthSpeed(Block block, BlockGetter blockGetter,
                                          BlockPos pos, CallbackInfoReturnable<Float> cir) {
        float originalSpeed = cir.getReturnValue();
        cir.setReturnValue(originalSpeed * ModConstants.CROP_GROWTH_MODIFIER);
    }
}