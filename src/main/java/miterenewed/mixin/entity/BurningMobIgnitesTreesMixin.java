package miterenewed.mixin.entity;

import miterenewed.ModConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * A burning mob next to a tree (flammable logs or leaves within 1 block) has a small chance
 * each tick to light a fire on it.
 */
@Mixin(Mob.class)
public abstract class BurningMobIgnitesTreesMixin {

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void mite$igniteNearbyTree(CallbackInfo ci) {
        Mob mob = (Mob) (Object) this;
        if (!(mob.level() instanceof ServerLevel level) || !mob.isOnFire()
                || mob.getRandom().nextFloat() >= ModConstants.BURNING_MOB_TREE_IGNITE_CHANCE
                || !level.getGameRules().get(GameRules.MOB_GRIEFING)) {
            return;
        }

        List<BlockPos> treeBlocks = new ArrayList<>();
        for (BlockPos pos : BlockPos.betweenClosed(mob.getBoundingBox().inflate(1.0))) {
            BlockState state = level.getBlockState(pos);
            if ((state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES)) && state.ignitedByLava()) {
                treeBlocks.add(pos.immutable());
            }
        }
        if (treeBlocks.isEmpty()) {
            return;
        }

        BlockPos tree = treeBlocks.get(mob.getRandom().nextInt(treeBlocks.size()));
        mite$placeFireNextTo(level, tree, Direction.getRandom(mob.getRandom()));
    }

    // Fire goes in an air block touching the tree block, trying each side starting from a random one
    @Unique
    private static void mite$placeFireNextTo(ServerLevel level, BlockPos tree, Direction first) {
        Direction[] directions = Direction.values();
        for (int i = 0; i < directions.length; i++) {
            Direction side = directions[(first.ordinal() + i) % directions.length];
            BlockPos firePos = tree.relative(side);
            if (BaseFireBlock.canBePlacedAt(level, firePos, side)) {
                level.setBlock(firePos, BaseFireBlock.getState(level, firePos), 11);
                return;
            }
        }
    }
}
