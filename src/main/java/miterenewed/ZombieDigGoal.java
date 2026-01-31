package miterenewed;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;

public class ZombieDigGoal extends Goal {
    private final Zombie mob;
    private BlockPos target;
    private float targetHardness;
    private float progress;
    private float ratio;

    public int maximumTargetHardness = 1;
    public float diggingProgressTick = 0.02f;
    public boolean dropBrokenBlocks = true;
    public boolean instantDoorBreak = true;
    public float instantDoorBreakHardness = 5f;

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
        if (target == null || mob == null) return false;
        double distSqr = mob.distanceToSqr(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
        return distSqr <= REACH_DISTANCE_SQR && this.progress <= this.targetHardness;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void tick() {
        progress += diggingProgressTick;

        if (mob.tickCount % 10 == 0) {
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
        if (targetEntity == null) return false;
        if (!mob.getNavigation().isInProgress()) {
            mob.getLookControl().setLookAt(targetEntity, 30.0F, 30.0F);
            // This pushes the zombie toward the player's location even without a path
            mob.getMoveControl().setWantedPosition(targetEntity.getX(), mob.getY(), targetEntity.getZ(), 1.0D);
        }

        BlockPos feetPos = mob.blockPosition();
        BlockPos headPos = feetPos.above();
        BlockPos belowFeetPos = feetPos.below();

        double dx = targetEntity.getX() - mob.getX();
        double dz = targetEntity.getZ() - mob.getZ();
        Direction dir;
        if (Math.abs(dx) > Math.abs(dz)) {
            dir = dx > 0 ? Direction.EAST : Direction.WEST;
        } else {
            dir = dz > 0 ? Direction.SOUTH : Direction.NORTH;
        }

        BlockPos blockInFrontHead = headPos.relative(dir);
        BlockPos blockInFrontFeet = feetPos.relative(dir);
        BlockPos blockBelowFeet = belowFeetPos.relative(dir);

        Level mobLevel = mob.level();
        BlockState stateHead = mobLevel.getBlockState(blockInFrontHead);
        BlockState stateFeet = mobLevel.getBlockState(blockInFrontFeet);
        BlockState stateBelowFeet = mobLevel.getBlockState(blockBelowFeet);

        boolean shouldDigAtHeadLevel = !stateHead.isAir() && stateHead.getShape(mobLevel, blockInFrontHead) != Shapes.empty();
        boolean shouldDigAtFeetLevel = !stateFeet.isAir() && stateFeet.getShape(mobLevel, blockInFrontFeet) != Shapes.empty();
        boolean shouldDigBelowFeetLevel = !stateBelowFeet.isAir() && stateBelowFeet.getShape(mobLevel, blockBelowFeet) != Shapes.empty();

        float hardness = 2f;
        if (shouldDigAtHeadLevel) {
            target = blockInFrontHead;
            hardness = stateHead.getDestroySpeed(mobLevel, target);
        } else if (shouldDigAtFeetLevel) {
            target = blockInFrontFeet;
            hardness = stateFeet.getDestroySpeed(mobLevel, target);
        } else if (shouldDigBelowFeetLevel) {
            target = blockBelowFeet;
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
}