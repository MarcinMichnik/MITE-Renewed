package miterenewed.mixin.entity.zombie;

import miterenewed.ZombieDigGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Zombie.class)
public abstract class ZombieBehaviourMixin extends Monster {

    protected ZombieBehaviourMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "addBehaviourGoals", at = @At("HEAD"))
    private void addDigGoal(CallbackInfo ci) {
        // Above ZombieAttackGoal (2) so digging takes over movement only when the target can't be walked to
        this.goalSelector.addGoal(1, new ZombieDigGoal((Zombie)(Object)this));
    }

    // Vanilla zombies drop the chase whenever their current path ends (partial path, target briefly out of
    // sight) and the attack goal can't restart for 20 ticks, so they stand still for a second. Keep chasing.
    @ModifyArg(method = "addBehaviourGoals", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/goal/ZombieAttackGoal;<init>(Lnet/minecraft/world/entity/monster/zombie/Zombie;DZ)V"),
            index = 2)
    private boolean keepChasingTarget(boolean followingTargetEvenIfNotSeen) {
        return true;
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void removeWanderGoal(CallbackInfo ci) {
        // Remove the goal that makes them stroll around when idle
        this.goalSelector.getAvailableGoals().removeIf(wrappedGoal ->
                wrappedGoal.getGoal() instanceof WaterAvoidingRandomStrollGoal
        );
    }

}