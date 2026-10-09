package miterenewed.mixin.entity;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Fish aren't Animals: they extend WaterAnimal, which has its own 1-3 XP reward
@Mixin({Animal.class, WaterAnimal.class})
public abstract class PassiveMobXpMixin {

    @Inject(method = "getBaseExperienceReward", at = @At("HEAD"), cancellable = true)
    private void zeroAnimalXp(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(0);
    }
}