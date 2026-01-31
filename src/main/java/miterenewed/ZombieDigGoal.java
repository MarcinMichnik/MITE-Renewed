package miterenewed;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;

public class ZombieDigGoal extends Goal {
    private final Zombie mob;
    private BlockPos target;
    private float targetHardness;
    private float progress;
    private float ratio;

    public int maximumTargetHardness = 20;
    public float diggingProgressTick = 0.05f;
    public boolean dropBrokenBlocks = true;
    public boolean instantDoorBreak = true;
    public float instantDoorBreakHardness = 5f;


    public ZombieDigGoal(Zombie mob) {
        this.mob = mob;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (target == null) return false;
        if (mob == null) return false;
        return blockPosDistance(mob, target) <= 3 && this.progress <= this.targetHardness;
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
        // Map 0.0-1.0 progress to 0-9 cracking animation
        int visualProgress = (int) (progress * ratio);
        mob.level().destroyBlockProgress(mob.getId(), target, visualProgress);
    }

    @Override
    public void stop() {
        mob.level().destroyBlockProgress(mob.getId(), target, 0);
        progress = 0;
        target = null;
        targetHardness = 0;
        mob.getNavigation().recomputePath();
    }

    @Override
    public boolean canUse() {
        LivingEntity targetEntity = mob.getTarget();
        if (targetEntity == null) return false;

        // Use foot position instead of block underneath
        BlockPos headPos = mob.blockPosition();
        Direction dir = mob.getDirection();
        BlockPos blockInFront = headPos.relative(dir);

        BlockState state = mob.level().getBlockState(blockInFront);

        // 1. Check for Doors (Instant Break)
        if (instantDoorBreak && state.getBlock() instanceof DoorBlock) {
            if (state.getDestroySpeed(mob.level(), blockInFront) <= instantDoorBreakHardness) {
                mob.level().destroyBlock(blockInFront, dropBrokenBlocks);
                return false;
            }
        }

        // 2. Check if the path is actually blocked
        // If the block in front isn't air and isn't something we can walk through...
        if (!state.isAir() && state.getShape(mob.level(), blockInFront) != Shapes.empty()) {
            target = blockInFront;
            float hardness = state.getDestroySpeed(mob.level(), target);

            if (hardness < 0 || hardness > maximumTargetHardness) return false;

            this.targetHardness = hardness * 20; // Scale to ticks
            this.ratio = 10.0f / this.targetHardness;
            return true;
        }

        return false;
    }

    public static float blockPosDistance(Entity entity, BlockPos pos) {
        return blockPosDistance(entity.getOnPos(), pos);
    }

    public static float blockPosDistance(BlockPos pos1, BlockPos pos2) {
        float x = (pos1.getX() - pos2.getX());
        float y = (pos1.getY() - pos2.getY());
        float z = (pos1.getZ() - pos2.getZ());
        return (float) Math.sqrt(x * x + y * y + z * z);
    }
}