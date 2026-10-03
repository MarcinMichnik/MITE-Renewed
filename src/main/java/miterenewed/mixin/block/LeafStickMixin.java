package miterenewed.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class LeafStickMixin {
    @Inject(method = "playerDestroy", at = @At("HEAD"))
    private void dropSticksOnLeafBreak(ServerLevel world, ServerPlayer player, BlockPos pos, BlockState state,
                                       BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
        if (state.is(BlockTags.LEAVES)) {
            // 10% chance to drop a stick
            if (world.getRandom().nextFloat() < 0.1f) {
                Block.popResource(world, pos, new ItemStack(Items.STICK));
            }
        }
    }
}