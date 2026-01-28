package miterenewed.mixin;


import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class BlockHardnessMixin {

    @Inject(method = "getHardness", at = @At("RETURN"), cancellable = true)
    private void removeInstantMining(BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        float hardness = cir.getReturnValue();
        BlockState state = (BlockState) (Object) this;

        // 1. Allow Torches (and other utility blocks) to stay instant
        if (state.isIn(BlockTags.WALL_POST_OVERRIDE) || state.isOf(Blocks.TORCH)
                || state.isOf(Blocks.WALL_TORCH) || state.isIn(BlockTags.SMALL_FLOWERS)) {
            return;
        }

        // 2. If the block is normally instant (0.0), give it a tiny bit of resistance
        if (hardness == 0.0F) {
            cir.setReturnValue(0.05F);
        }
    }
}