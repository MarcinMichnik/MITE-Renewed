package miterenewed.mixin.entity;

import miterenewed.ModConstants;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Makes fish harder to catch by hand: they notice players from further away and swim off faster,
 * both when a player approaches and when they get hurt.
 */
@Mixin(AbstractFish.class)
public class FishEvasionMixin {
    private static final String AVOID_PLAYER_GOAL =
            "Lnet/minecraft/world/entity/ai/goal/AvoidEntityGoal;<init>(Lnet/minecraft/world/entity/PathfinderMob;Ljava/lang/Class;FDDLjava/util/function/Predicate;)V";

    @ModifyArg(method = "registerGoals", at = @At(value = "INVOKE", target = AVOID_PLAYER_GOAL), index = 2)
    private float mite$avoidPlayerRange(float maxDist) {
        return ModConstants.FISH_AVOID_PLAYER_RANGE;
    }

    // Used while the player is further than 7 blocks away
    @ModifyArg(method = "registerGoals", at = @At(value = "INVOKE", target = AVOID_PLAYER_GOAL), index = 3)
    private double mite$avoidPlayerWalkSpeed(double walkSpeedModifier) {
        return ModConstants.FISH_AVOID_PLAYER_SPEED;
    }

    // Used while the player is within 7 blocks
    @ModifyArg(method = "registerGoals", at = @At(value = "INVOKE", target = AVOID_PLAYER_GOAL), index = 4)
    private double mite$avoidPlayerSprintSpeed(double sprintSpeedModifier) {
        return ModConstants.FISH_AVOID_PLAYER_SPEED;
    }

    // AnimalPanicMixin then applies its own multiplier on top, like for other animals
    @ModifyArg(method = "registerGoals", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/goal/PanicGoal;<init>(Lnet/minecraft/world/entity/PathfinderMob;D)V"), index = 1)
    private double mite$panicSpeed(double speedModifier) {
        return ModConstants.FISH_PANIC_SPEED;
    }
}
