package miterenewed.mixin.block;

import miterenewed.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Gives blocks in the {@code mite-renewed:falls_like_sand} tag (dirt-like blocks) the same gravity
 * behaviour as {@link FallingBlock}: they fall when placed or when the block below is removed.
 * <p>
 * Unlike sand, a fall check is only scheduled when the space below is actually free. Some tagged
 * blocks (dirt paths, farmland) have their own scheduled-tick logic that turns them into dirt,
 * so an unconditional tick would revert them as soon as they are created.
 */
@Mixin(BlockBehaviour.class)
public abstract class FallingDirtMixin {
    @Unique
    private static final TagKey<Block> FALLS_LIKE_SAND =
            TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ModItems.MOD_ID, "falls_like_sand"));

    // Same delay vanilla FallingBlock uses
    @Unique
    private static final int FALL_DELAY_TICKS = 2;

    @Inject(method = "onPlace", at = @At("HEAD"))
    private void mite$scheduleFallOnPlace(BlockState state, Level level, BlockPos pos, BlockState oldState,
                                          boolean movedByPiston, CallbackInfo ci) {
        if (state.is(FALLS_LIKE_SAND) && FallingBlock.isFree(level.getBlockState(pos.below()))) {
            level.scheduleTick(pos, state.getBlock(), FALL_DELAY_TICKS);
        }
    }

    @Inject(method = "updateShape", at = @At("HEAD"))
    private void mite$scheduleFallOnNeighborChange(BlockState state, LevelReader level, ScheduledTickAccess ticks,
                                                   BlockPos pos, Direction direction, BlockPos neighborPos,
                                                   BlockState neighborState, RandomSource random,
                                                   CallbackInfoReturnable<BlockState> cir) {
        if (direction == Direction.DOWN && state.is(FALLS_LIKE_SAND) && FallingBlock.isFree(neighborState)) {
            ticks.scheduleTick(pos, state.getBlock(), FALL_DELAY_TICKS);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void mite$fall(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (state.is(FALLS_LIKE_SAND)
                && FallingBlock.isFree(level.getBlockState(pos.below()))
                && pos.getY() >= level.getMinY()) {
            FallingBlockEntity.fall(level, pos, state);
            // The block is gone; skip block-specific tick logic (e.g. path/farmland turning into dirt)
            ci.cancel();
        }
    }
}
