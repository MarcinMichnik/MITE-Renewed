package miterenewed.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Leaves have no collision for dropped items, so items fall through tree canopies to the ground.
 * Players and mobs still collide with leaves as usual.
 */
@Mixin(BlockBehaviour.class)
public abstract class ItemsFallThroughLeavesMixin {

    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void mite$noLeafCollisionForItems(BlockState state, BlockGetter level, BlockPos pos,
                                              CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        // Called for every block collision: cheap instanceof checks before the tag lookup
        if (context instanceof EntityCollisionContext entityContext
                && entityContext.getEntity() instanceof ItemEntity
                && state.is(BlockTags.LEAVES)) {
            cir.setReturnValue(Shapes.empty());
        }
    }
}
