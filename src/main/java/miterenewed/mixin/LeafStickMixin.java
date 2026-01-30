package miterenewed.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
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
    private void dropSticksOnLeafBreak(Level world, Player player, BlockPos pos, BlockState state,
                                       BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
        if (world.isClientSide()) return;

        if (state.is(BlockTags.LEAVES)) {
            // 10% chance to drop a stick
            if (world.random.nextFloat() < 0.1f) {
                Block.popResource(world, pos, new ItemStack(Items.STICK));
            }
        }
    }
}