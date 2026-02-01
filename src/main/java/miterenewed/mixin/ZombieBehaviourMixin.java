package miterenewed.mixin;

import miterenewed.ZombieDigGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Zombie.class)
public abstract class ZombieBehaviourMixin extends Monster {

    protected ZombieBehaviourMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "addBehaviourGoals", at = @At("HEAD"))
    private void addDigGoal(CallbackInfo ci) {
        this.goalSelector.addGoal(3, new ZombieDigGoal((Zombie)(Object)this));
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void removeWanderGoal(CallbackInfo ci) {
        // Remove the goal that makes them stroll around when idle
        this.goalSelector.getAvailableGoals().removeIf(wrappedGoal ->
                wrappedGoal.getGoal() instanceof WaterAvoidingRandomStrollGoal
        );
    }

}