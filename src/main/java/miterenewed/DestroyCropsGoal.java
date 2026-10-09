package miterenewed;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.EnumSet;

/**
 * Idle hostile mobs walk up to nearby crops and destroy them. Same idea as the vanilla rabbit
 * raiding carrots: {@code blockPos} is the farmland, the crop sits on top of it.
 * <p>
 * Never runs while the mob has a target, so chasing players and animals always comes first.
 */
public class DestroyCropsGoal extends MoveToBlockGoal {
    private static final int SEARCH_RANGE = 16;
    private static final int VERTICAL_SEARCH_RANGE = 3;
    private static final int GIVE_UP_TICKS = 200; // can't reach the crop within 10 s
    private static final int NEXT_CROP_DELAY_TICKS = 10; // keep going through a field without the usual search delay

    private int ticksAtCrop;

    public DestroyCropsGoal(PathfinderMob mob) {
        super(mob, 1.0, SEARCH_RANGE, VERTICAL_SEARCH_RANGE);
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (mob.getTarget() != null || !getServerLevel(mob).getGameRules().get(GameRules.MOB_GRIEFING)) {
            return false;
        }
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return mob.getTarget() == null && tryTicks <= GIVE_UP_TICKS && super.canContinueToUse();
    }

    @Override
    protected int nextStartTick(PathfinderMob mob) {
        return reducedTickDelay(ModConstants.MOB_CROP_SEARCH_INTERVAL_TICKS
                + mob.getRandom().nextInt(ModConstants.MOB_CROP_SEARCH_INTERVAL_TICKS));
    }

    @Override
    public void start() {
        super.start();
        ticksAtCrop = 0;
    }

    @Override
    public double acceptedDistance() {
        return 1.5;
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos crop = blockPos.above();
        mob.getLookControl().setLookAt(crop.getX() + 0.5, crop.getY() + 0.5, crop.getZ() + 0.5, 10.0F, mob.getMaxHeadXRot());
        if (!isReachedTarget()) {
            return;
        }

        if (++ticksAtCrop < ModConstants.MOB_CROP_DESTROY_TICKS) {
            return;
        }
        mob.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
        mob.level().destroyBlock(crop, ModConstants.MOB_DESTROYED_CROPS_DROP, mob);
        nextStartTick = NEXT_CROP_DELAY_TICKS;
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        return level.getBlockState(pos).is(BlockTags.SUPPORTS_CROPS) && level.getBlockState(pos.above()).is(BlockTags.CROPS);
    }
}
