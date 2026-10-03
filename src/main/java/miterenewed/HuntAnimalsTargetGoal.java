package miterenewed;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.spider.Spider;

/** Makes a hostile mob target nearby animals (chickens, pigs, cows...), but not hostile "animals" like hoglins. */
public class HuntAnimalsTargetGoal extends NearestAttackableTargetGoal<Animal> {
    public HuntAnimalsTargetGoal(Mob mob) {
        super(mob, Animal.class, 10, true, false, (target, level) -> !(target instanceof Enemy));
    }

    @Override
    public boolean canUse() {
        // Spiders (and cave spiders) only go hunting in the dark, same rule vanilla uses for players.
        // Their attack goal already makes them lose interest when it gets bright.
        if (mob instanceof Spider && mob.getLightLevelDependentMagicValue() >= 0.5F) {
            return false;
        }
        return super.canUse();
    }
}
