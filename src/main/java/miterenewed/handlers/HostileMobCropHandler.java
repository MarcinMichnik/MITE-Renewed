package miterenewed.handlers;

import miterenewed.DestroyCropsGoal;
import miterenewed.mixin.entity.MobGoalsAccessor;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Monster;

/**
 * Hostile mobs that walk on the ground go out of their way to destroy crops when they have nothing
 * to attack. The goal goes right below the attack goal, so it beats idle wandering and looking
 * around, but any target interrupts it. Neutral mobs (endermen, zombified piglins) leave crops alone.
 */
public class HostileMobCropHandler {
    private static final int DEFAULT_ATTACK_PRIORITY = 4;

    public static void init() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (!(entity instanceof Monster monster) || entity instanceof NeutralMob
                    || !(monster.getNavigation() instanceof GroundPathNavigation)) {
                return;
            }
            GoalSelector goals = ((MobGoalsAccessor) monster).mite$getGoalSelector();
            int attackPriority = -1;
            for (WrappedGoal goal : goals.getAvailableGoals()) {
                if (goal.getGoal() instanceof DestroyCropsGoal) {
                    return;
                }
                if (isAttackGoal(goal)) {
                    attackPriority = Math.max(attackPriority, goal.getPriority());
                }
            }
            if (attackPriority < 0) {
                attackPriority = DEFAULT_ATTACK_PRIORITY;
            }
            goals.addGoal(attackPriority + 1, new DestroyCropsGoal(monster));
        });
    }

    private static boolean isAttackGoal(WrappedGoal wrapped) {
        var goal = wrapped.getGoal();
        return goal instanceof MeleeAttackGoal || goal instanceof RangedAttackGoal
                || goal instanceof RangedBowAttackGoal<?> || goal instanceof RangedCrossbowAttackGoal<?>;
    }
}
