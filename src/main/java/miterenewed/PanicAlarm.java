package miterenewed;

import miterenewed.mixin.entity.MobGoalsAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.Animal;
import org.jetbrains.annotations.Nullable;

/** Implemented on PanicGoal: lets a herd member raise the alarm on its neighbours. */
public interface PanicAlarm {
    void mite$alarm(@Nullable LivingEntity threat);

    /** Makes every animal around {@code victim} (itself included) flee from {@code attacker}. */
    static void alertNearbyAnimals(Animal victim, LivingEntity attacker) {
        var area = victim.getBoundingBox().inflate(ModConstants.HERD_ALERT_RADIUS);
        // Pets are left out: they would run from their owner instead of defending them
        for (Animal animal : victim.level().getEntitiesOfClass(Animal.class, area,
                e -> e.isAlive() && !(e instanceof TamableAnimal pet && pet.isTame()))) {
            for (WrappedGoal goal : ((MobGoalsAccessor) animal).mite$getGoalSelector().getAvailableGoals()) {
                if (goal.getGoal() instanceof PanicAlarm alarm) {
                    alarm.mite$alarm(attacker);
                }
            }
        }
    }
}
