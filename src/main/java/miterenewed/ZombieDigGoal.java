package miterenewed;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.shapes.Shapes;

public class ZombieDigGoal extends Goal {
    private final Zombie mob;
    private BlockPos target;
    private float targetHardness;
    private float progress;
    private float ratio;

    private Block targetBlock;

    public int maximumTargetHardness = 1;
    public float diggingProgressTick = 0.03f;
    public boolean dropBrokenBlocks = true;

    private static final double REACH_DISTANCE_SQR = 4.0;


    public ZombieDigGoal(Zombie mob) {
        this.mob = mob;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (target == null || mob == null || targetBlock == null) return false;

        Block currentBlock = mob.level().getBlockState(target).getBlock();
        if (currentBlock != targetBlock) {
            return false;
        }

        if (mob.hurtTime > 0) return false;

        if (mob.getTarget() != null) {
            Path path = mob.getNavigation().createPath(mob.getTarget(), 0);
            if (path != null && path.canReach()) {
                return false;
            }

            Direction dir = getDirectionToPlayer(mob, mob.getTarget());
            BlockPos feetInFront = mob.blockPosition().relative(dir);
            BlockPos BelowFeetInFront = mob.blockPosition().below().relative(dir);
            BlockPos headInFront = mob.blockPosition().above().relative(dir);
            BlockPos aboveHeadInFront = mob.blockPosition().above().above().relative(dir);

            boolean pathIsClearInFront = mob.level().getBlockState(feetInFront).isAir() &&
                    mob.level().getBlockState(headInFront).isAir();
            boolean pathIsClearInFrontAndAbove = mob.level().getBlockState(headInFront).isAir() &&
                    mob.level().getBlockState(aboveHeadInFront).isAir();
            boolean pathIsClearInFrontAndBelow = mob.level().getBlockState(feetInFront).isAir() &&
                    mob.level().getBlockState(BelowFeetInFront).isAir();
            if (pathIsClearInFront || pathIsClearInFrontAndAbove || pathIsClearInFrontAndBelow) {
                return false;
            }
        }

        double distSqr = mob.distanceToSqr(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
        return distSqr <= REACH_DISTANCE_SQR && this.progress <= this.targetHardness;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void tick() {
        if (target == null) return;

        LivingEntity targetEntity = mob.getTarget();
        if (targetEntity != null) {
            mob.getNavigation().stop();
            mob.getLookControl().setLookAt(targetEntity.getX() + 0.5, targetEntity.getY() + 0.5, targetEntity.getZ() + 0.5, 30.0F, 30.0F);
            mob.getMoveControl().setWantedPosition(targetEntity.getX(), targetEntity.getY(), targetEntity.getZ(), 0.0D);
        }

        progress += diggingProgressTick;

        if (mob.tickCount % 5 == 0) {
            mob.swing(InteractionHand.MAIN_HAND);
        }

        if (progress >= targetHardness) {
            mob.level().destroyBlock(this.target, dropBrokenBlocks);
            return;
        }
        int visualProgress = (int) (progress * ratio);
        mob.level().destroyBlockProgress(mob.getId(), target, visualProgress);
    }

    @Override
    public void stop() {
        mob.level().destroyBlockProgress(mob.getId(), target, -1);
        progress = 0;
        target = null;
        targetHardness = 0;
        mob.getNavigation().recomputePath();
    }

    @Override
    public boolean canUse() {
        LivingEntity targetEntity = mob.getTarget();
        if (targetEntity == null) {
            return false;
        }
        Path path = mob.getNavigation().createPath(mob.getTarget(), 0);

        if (path != null) {
            boolean canReach = path.canReach();
            if (canReach) {
                return false;
            }
        }

        Direction dir = getDirectionToPlayer(mob, mob.getTarget());
        BlockPos feetInFront = mob.blockPosition().relative(dir);
        BlockPos BelowFeetInFront = mob.blockPosition().below().relative(dir);
        BlockPos headInFront = mob.blockPosition().above().relative(dir);
        BlockPos aboveHeadInFront = mob.blockPosition().above().above().relative(dir);

        boolean pathIsClearInFront = mob.level().getBlockState(feetInFront).isAir() &&
                mob.level().getBlockState(headInFront).isAir();
        boolean pathIsClearInFrontAndAbove = mob.level().getBlockState(headInFront).isAir() &&
                mob.level().getBlockState(aboveHeadInFront).isAir();
        boolean pathIsClearInFrontAndBelow = mob.level().getBlockState(feetInFront).isAir() &&
                mob.level().getBlockState(BelowFeetInFront).isAir();
        if (pathIsClearInFront || pathIsClearInFrontAndAbove || pathIsClearInFrontAndBelow) {
            return false;
        }

        BlockPos feetPos = mob.blockPosition();
        BlockPos headPos = feetPos.above();
        BlockPos belowFeetPos = feetPos.below();

        BlockPos blockInFrontHead = headPos.relative(dir);
        BlockPos blockInFrontFeet = feetPos.relative(dir);
        BlockPos blockBelowFeet = belowFeetPos.relative(dir);

        Level mobLevel = mob.level();
        BlockState stateHead = mobLevel.getBlockState(blockInFrontHead);
        BlockState stateFeet = mobLevel.getBlockState(blockInFrontFeet);
        BlockState stateBelowFeet = mobLevel.getBlockState(blockBelowFeet);

        boolean shouldDigAtHeadLevel = !stateHead.isAir() && stateHead.getShape(mobLevel, blockInFrontHead) != Shapes.empty();
        boolean shouldDigAtFeetLevel = !stateFeet.isAir() && stateFeet.getShape(mobLevel, blockInFrontFeet) != Shapes.empty();
        boolean shouldDigBelowFeetLevel = !stateBelowFeet.isAir() &&
                stateBelowFeet.getShape(mobLevel, blockBelowFeet) != Shapes.empty() &&
                targetEntity.getY() < mob.getY();

        float hardness = 2f;
        if (shouldDigAtHeadLevel) {
            target = blockInFrontHead;
            this.targetBlock = mobLevel.getBlockState(target).getBlock();
            hardness = stateHead.getDestroySpeed(mobLevel, target);
        } else if (shouldDigAtFeetLevel) {
            target = blockInFrontFeet;
            this.targetBlock = mobLevel.getBlockState(target).getBlock();
            hardness = stateFeet.getDestroySpeed(mobLevel, target);
        } else if (shouldDigBelowFeetLevel) {
            target = blockBelowFeet;
            this.targetBlock = mobLevel.getBlockState(target).getBlock();
            hardness = stateBelowFeet.getDestroySpeed(mobLevel, target);
        }

        if (shouldDigAtFeetLevel || shouldDigAtHeadLevel || shouldDigBelowFeetLevel) {
            if (hardness < 0 || hardness > maximumTargetHardness) return false;
            this.targetHardness = hardness * 20;
            this.ratio = 10.0f / this.targetHardness;
            return true;
        }

        return false;
    }

    private Direction getDirectionToPlayer(Zombie mob, LivingEntity target) {
        double dx = target.getX() - mob.getX();
        double dy = target.getY() - mob.getY();
        double dz = target.getZ() - mob.getZ();
        return Direction.getApproximateNearest(dx, dy, dz);
    }
}