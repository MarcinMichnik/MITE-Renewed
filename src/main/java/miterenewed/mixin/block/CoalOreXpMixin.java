package miterenewed.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import miterenewed.ModConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Coal ores always drop some XP when mined; vanilla rolls 0-2, so a third of them give nothing. */
@Mixin(DropExperienceBlock.class)
public abstract class CoalOreXpMixin {
    @WrapOperation(method = "spawnAfterBreak", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/DropExperienceBlock;tryDropExperience(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/util/valueproviders/IntProvider;)V"))
    private void mite$coalOreXp(DropExperienceBlock block, ServerLevel level, BlockPos pos, ItemStack tool,
                                IntProvider xpRange, Operation<Void> original) {
        if (block == Blocks.COAL_ORE || block == Blocks.DEEPSLATE_COAL_ORE) {
            xpRange = UniformInt.of(ModConstants.COAL_ORE_MIN_XP, ModConstants.COAL_ORE_MAX_XP);
        }
        original.call(block, level, pos, tool, xpRange);
    }
}
