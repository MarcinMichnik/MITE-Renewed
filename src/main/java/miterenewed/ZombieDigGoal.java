package miterenewed;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

/**
 * MITE-style tunnelling: when a zombie cannot walk to its target, it digs a 1x2 tunnel towards it,
 * one step at a time (level, staircase up, staircase down or straight down).
 * <p>
 * The goal runs above the vanilla attack goal and owns movement while active, so walking and
 * digging never fight over the navigation. It only takes over when the zombie is stuck and a
 * fresh path check confirms the target is unreachable on foot; after every step it re-checks
 * and hands control back as soon as a walkable route exists.
 */
public class ZombieDigGoal extends Goal {
    private static final double MAX_DIG_REACH_SQR = 3.0 * 3.0;
    private static final int PATH_CHECK_INTERVAL = 10;
    private static final int ADVANCE_TIMEOUT_TICKS = 40;
    private static final int GIVE_UP_COOLDOWN_TICKS = 40;

    private enum Phase { DIG, ADVANCE }

    /** Blocks to clear (in order), then the cell to move into. */
    private record Step(List<BlockPos> toDig, BlockPos destination, boolean climbing) {}

    private final Zombie mob;

    @Nullable private Step step;
    private Phase phase = Phase.DIG;
    @Nullable private BlockPos diggingPos;
    @Nullable private BlockState diggingState;
    private int digTicks;
    private int requiredDigTicks;
    private int advanceTicks;

    // Stuck detection, measured in mob ticks because canUse is not called every tick
    private Vec3 lastMovedPos = Vec3.ZERO;
    private int lastMovedTick;
    private int nextPathCheckTick;
    private int cooldownUntilTick;

    public ZombieDigGoal(Zombie mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        trackMovement();

        LivingEntity target = mob.getTarget();
        if (!isValidTarget(target) || mob.tickCount < cooldownUntilTick || !mob.onGround()) {
            return false;
        }

        // Only consider digging once walking has stopped getting us anywhere
        boolean stuck = mob.tickCount - lastMovedTick >= ModConstants.ZOMBIE_STUCK_TICKS;
        if (!stuck && !mob.getNavigation().isDone()) {
            return false;
        }

        if (mob.tickCount < nextPathCheckTick) {
            return false;
        }
        nextPathCheckTick = mob.tickCount + PATH_CHECK_INTERVAL;

        if (canWalkTo(target)) {
            return false;
        }

        step = planStep(target);
        if (step == null) {
            cooldownUntilTick = mob.tickCount + GIVE_UP_COOLDOWN_TICKS;
        }
        return step != null;
    }

    @Override
    public boolean canContinueToUse() {
        return step != null && isValidTarget(mob.getTarget());
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void start() {
        mob.getNavigation().stop();
        beginStep();
    }

    @Override
    public void stop() {
        clearDigProgress();
        step = null;
        // Let the attack goal recompute its path from the new position right away
        lastMovedPos = mob.position();
        lastMovedTick = mob.tickCount;
        nextPathCheckTick = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (step == null || target == null) {
            return;
        }

        if (phase == Phase.DIG) {
            tickDig();
        } else {
            tickAdvance(target);
        }
    }

    private void tickDig() {
        BlockPos pos = diggingPos;
        Level level = mob.level();
        BlockState state = level.getBlockState(pos);

        // Block broken by someone else, or replaced (e.g. gravel fell in): pick the work again
        if (state != diggingState) {
            nextDigTarget();
            return;
        }

        if (mob.getEyePosition().distanceToSqr(Vec3.atCenterOf(pos)) > MAX_DIG_REACH_SQR) {
            replanOrGiveUp();
            return;
        }

        mob.getNavigation().stop();
        mob.getMoveControl().setWantedPosition(mob.getX(), mob.getY(), mob.getZ(), 0.0);
        mob.getLookControl().setLookAt(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        digTicks++;
        if (digTicks % 4 == 0) {
            mob.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
            SoundType sound = state.getSoundType();
            level.playSound(null, pos, sound.getHitSound(), SoundSource.HOSTILE,
                    (sound.getVolume() + 1.0F) / 8.0F, sound.getPitch() * 0.5F);
        }

        if (digTicks >= requiredDigTicks) {
            level.destroyBlock(pos, ModConstants.ZOMBIE_DIG_DROPS_BLOCKS, mob);
            nextDigTarget();
        } else {
            level.destroyBlockProgress(mob.getId(), pos, digTicks * 10 / requiredDigTicks);
        }
    }

    private void tickAdvance(LivingEntity target) {
        BlockPos dest = step.destination();
        mob.getMoveControl().setWantedPosition(dest.getX() + 0.5, dest.getY(), dest.getZ() + 0.5, 1.0);
        mob.getLookControl().setLookAt(target);
        if (step.climbing() && mob.onGround() && mob.horizontalCollision) {
            mob.getJumpControl().jump();
        }

        BlockPos feet = mob.blockPosition();
        // Feet at or below the destination: covers stepping up onto it as well as dropping into it
        boolean arrived = feet.getX() == dest.getX() && feet.getZ() == dest.getZ()
                && feet.getY() <= dest.getY() && mob.onGround();
        if (arrived) {
            // Through the wall? Hand back to the attack goal; otherwise keep tunnelling
            if (canWalkTo(target)) {
                step = null;
            } else {
                replanOrGiveUp();
            }
        } else if (++advanceTicks > ADVANCE_TIMEOUT_TICKS) {
            replanOrGiveUp();
        }
    }

    private void beginStep() {
        phase = Phase.DIG;
        advanceTicks = 0;
        nextDigTarget();
    }

    /**
     * Targets the first block of the current step that is still solid, or switches to the advance phase.
     * Rescans from the start so blocks that fell back in (sand, gravel) are dug again.
     */
    private void nextDigTarget() {
        clearDigProgress();
        Level level = mob.level();
        for (BlockPos pos : step.toDig()) {
            BlockState state = level.getBlockState(pos);
            if (isPassable(state, pos)) {
                continue;
            }
            if (!canDig(state, pos)) {
                replanOrGiveUp();
                return;
            }
            diggingPos = pos;
            diggingState = state;
            digTicks = 0;
            requiredDigTicks = Math.max(ModConstants.ZOMBIE_MIN_DIG_TICKS,
                    Math.round(state.getDestroySpeed(level, pos) * ModConstants.ZOMBIE_DIG_TICKS_PER_HARDNESS));
            if (mob instanceof DiggingMob diggingMob) {
                diggingMob.setDiggingTarget(pos);
            }
            return;
        }
        phase = Phase.ADVANCE;
        advanceTicks = 0;
    }

    private void replanOrGiveUp() {
        clearDigProgress();
        LivingEntity target = mob.getTarget();
        step = isValidTarget(target) && mob.onGround() ? planStep(target) : null;
        if (step != null) {
            beginStep();
        } else {
            cooldownUntilTick = mob.tickCount + GIVE_UP_COOLDOWN_TICKS;
        }
    }

    private void clearDigProgress() {
        if (mob instanceof DiggingMob diggingMob && diggingMob.getDiggingTarget() != null) {
            mob.level().destroyBlockProgress(mob.getId(), diggingMob.getDiggingTarget(), -1);
            diggingMob.setDiggingTarget(null);
        }
        diggingPos = null;
        diggingState = null;
        digTicks = 0;
    }

    // ---- Decision making ----

    private boolean isValidTarget(@Nullable LivingEntity target) {
        // Only tunnel towards players, not every animal or villager the zombie is chasing
        return target instanceof Player && target.isAlive()
                && mob.level() instanceof ServerLevel serverLevel
                && serverLevel.getGameRules().get(GameRules.MOB_GRIEFING)
                && !mob.isWithinMeleeAttackRange(target);
    }

    private boolean canWalkTo(LivingEntity target) {
        Path path = mob.getNavigation().createPath(target, 0);
        return path != null && path.canReach();
    }

    private void trackMovement() {
        if (mob.position().distanceToSqr(lastMovedPos) > 1.0) {
            lastMovedPos = mob.position();
            lastMovedTick = mob.tickCount;
        }
    }

    /** Picks the most direct diggable step towards the target, or null if there is none. */
    @Nullable
    private Step planStep(LivingEntity target) {
        BlockPos feet = mob.blockPosition();
        double dx = target.getX() - mob.getX();
        double dz = target.getZ() - mob.getZ();
        int dy = target.getBlockY() - feet.getY();

        if (dx * dx + dz * dz < 1.0) {
            // Directly above can't be reached without building; directly below: dig straight down
            return dy < 0 ? buildStep(List.of(feet.below()), feet.below(), false) : null;
        }

        Direction primary = Math.abs(dx) >= Math.abs(dz)
                ? (dx > 0 ? Direction.EAST : Direction.WEST)
                : (dz > 0 ? Direction.SOUTH : Direction.NORTH);
        Direction secondary = Math.abs(dx) >= Math.abs(dz)
                ? (dz > 0 ? Direction.SOUTH : Direction.NORTH)
                : (dx > 0 ? Direction.EAST : Direction.WEST);
        double secondaryComponent = Math.abs(dx) >= Math.abs(dz) ? Math.abs(dz) : Math.abs(dx);

        List<Direction> directions = secondaryComponent > 0.5 ? List.of(primary, secondary) : List.of(primary);
        for (Direction dir : directions) {
            BlockPos front = feet.relative(dir);
            if (dy > 0) {
                Step up = stepUp(feet, front);
                if (up != null) return up;
            } else if (dy < 0) {
                Step down = buildStep(List.of(front.above(), front, front.below()), front.below(), false);
                if (down != null) return down;
            }
            Step level = buildStep(List.of(front.above(), front), front, false);
            if (level != null) return level;
        }
        return null;
    }

    @Nullable
    private Step stepUp(BlockPos feet, BlockPos front) {
        // Climbing needs something solid in front to step onto
        BlockState frontState = mob.level().getBlockState(front);
        if (isPassable(frontState, front)) {
            return null;
        }
        return buildStep(List.of(feet.above(2), front.above(2), front.above()), front.above(), true);
    }

    /** Returns a step only if every obstructing block is diggable and at least one needs digging. */
    @Nullable
    private Step buildStep(List<BlockPos> blocks, BlockPos destination, boolean climbing) {
        Level level = mob.level();
        boolean needsDigging = false;
        for (BlockPos pos : blocks) {
            BlockState state = level.getBlockState(pos);
            if (!state.getFluidState().isEmpty()) {
                return null;
            }
            if (isPassable(state, pos)) {
                continue;
            }
            if (!canDig(state, pos)) {
                return null;
            }
            needsDigging = true;
        }
        return needsDigging ? new Step(blocks, destination, climbing) : null;
    }

    private boolean isPassable(BlockState state, BlockPos pos) {
        return state.getFluidState().isEmpty() && state.getCollisionShape(mob.level(), pos).isEmpty();
    }

    private boolean canDig(BlockState state, BlockPos pos) {
        Level level = mob.level();
        float hardness = state.getDestroySpeed(level, pos);
        if (hardness < 0 || hardness > ModConstants.ZOMBIE_DIG_MAX_HARDNESS || state.hasBlockEntity()) {
            return false;
        }
        // Never open a path for water or lava
        for (Direction dir : Direction.values()) {
            if (!level.getFluidState(pos.relative(dir)).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
