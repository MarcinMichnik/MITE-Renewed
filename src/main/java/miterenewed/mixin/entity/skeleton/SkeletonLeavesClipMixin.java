package miterenewed.mixin.entity.skeleton;

import miterenewed.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes leaves transparent to ray traces done by skeletons and their arrows: skeletons can see
 * (and so decide to shoot) through foliage, and their arrows fly through it.
 */
@Mixin(ClipContext.class)
public abstract class SkeletonLeavesClipMixin {
    @Shadow @Final private CollisionContext collisionContext;

    @Inject(method = "getBlockShape", at = @At("HEAD"), cancellable = true)
    private void mite$leavesTransparentForSkeletons(BlockState state, BlockGetter level, BlockPos pos,
                                         CallbackInfoReturnable<VoxelShape> cir) {
        if (this.collisionContext instanceof EntityCollisionContext context
                && Utils.seesThroughLeaves(context.getEntity(), state)) {
            cir.setReturnValue(Shapes.empty());
        }
    }
}
