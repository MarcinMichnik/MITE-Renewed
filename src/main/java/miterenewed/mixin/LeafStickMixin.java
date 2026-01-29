package miterenewed.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class LeafStickMixin {
    @Inject(method = "afterBreak", at = @At("HEAD"))
    private void dropSticksOnLeafBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
        if (!world.isClient() && state.isIn(BlockTags.LEAVES)) {
            // 10% chance to drop a stick
            if (world.random.nextFloat() < 0.1f) {
                Block.dropStack(world, pos, new ItemStack(Items.STICK));
            }
        }
    }
}