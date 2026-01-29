package miterenewed.mixin;


import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockHardnessMixin {

    @Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
    private void removeInstantMining(BlockGetter world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        float hardness = cir.getReturnValue();
        BlockState state = (BlockState) (Object) this;

        // 1. Allow Torches (and other utility blocks) to stay instant
        if (state.is(BlockTags.WALL_POST_OVERRIDE) || state.is(Blocks.TORCH)
                || state.is(Blocks.WALL_TORCH) || state.is(BlockTags.SMALL_FLOWERS)) {
            return;
        }

        // 2. If the block is normally instant (0.0), give it a bit of resistance
        if (state.is(Blocks.SUGAR_CANE)) {
            cir.setReturnValue(0.25F);
            return;
        }
        if (hardness == 0.0F) {
            cir.setReturnValue(0.05F);
        }
    }
}