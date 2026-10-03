package miterenewed.mixin.entity.skeleton;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import miterenewed.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Stops a skeleton's arrow from counting as stuck in the ground while it is inside leaves. */
@Mixin(AbstractArrow.class)
public abstract class ArrowLeavesInGroundMixin {
    @WrapOperation(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape mite$passThroughLeaves(BlockState state, BlockGetter level, BlockPos pos,
                                              Operation<VoxelShape> original) {
        if (Utils.seesThroughLeaves((AbstractArrow) (Object) this, state)) {
            return Shapes.empty();
        }
        return original.call(state, level, pos);
    }
}
