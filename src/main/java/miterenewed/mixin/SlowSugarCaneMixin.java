package miterenewed.mixin;

import miterenewed.ModConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SugarCaneBlock.class)
public class SlowSugarCaneMixin {

    @Inject(method = "randomTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"),
            cancellable = true)
    private void mite$slowDownSugarCane(BlockState state, ServerLevel level, BlockPos pos,
                                        RandomSource random, CallbackInfo ci) {
        if (random.nextFloat() > ModConstants.CROP_GROWTH_MODIFIER) {
            ci.cancel();
        }
    }
}