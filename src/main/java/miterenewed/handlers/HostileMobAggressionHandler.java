package miterenewed.handlers;

import miterenewed.HuntAnimalsTargetGoal;
import miterenewed.mixin.entity.MobGoalsAccessor;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;

/**
 * Hostile mobs also hunt animals. The animal target goal is added below every existing target
 * goal, so players (and villagers, golems...) are still preferred and steal the mob's attention.
 * <p>
 * Only mobs that can actually attack (melee or ranged attack goal) get it. Creepers are excluded so
 * they don't blow up every animal pen, and neutral mobs (endermen, zombified piglins) stay neutral.
 */
public class HostileMobAggressionHandler {
    public static void init() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (!(entity instanceof Monster monster) || entity instanceof Creeper || entity instanceof NeutralMob) {
                return;
            }
            MobGoalsAccessor goals = (MobGoalsAccessor) monster;
            if (!canAttack(goals.mite$getGoalSelector())) {
                return;
            }
            GoalSelector targets = goals.mite$getTargetSelector();
            int lowestPriority = 0;
            for (WrappedGoal goal : targets.getAvailableGoals()) {
                if (goal.getGoal() instanceof HuntAnimalsTargetGoal) {
                    return;
                }
                lowestPriority = Math.max(lowestPriority, goal.getPriority());
            }
            targets.addGoal(lowestPriority + 1, new HuntAnimalsTargetGoal(monster));
        });
    }

    private static boolean canAttack(GoalSelector goalSelector) {
        return goalSelector.getAvailableGoals().stream().map(WrappedGoal::getGoal).anyMatch(goal ->
                goal instanceof MeleeAttackGoal || goal instanceof RangedAttackGoal
                        || goal instanceof RangedBowAttackGoal<?> || goal instanceof RangedCrossbowAttackGoal<?>);
    }
}
