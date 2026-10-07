package miterenewed.mixin.entity;

import miterenewed.PanicAlarm;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Warns every nearby animal whenever one is hurt by a living attacker. Done on damage rather than in
 * PanicGoal.canUse, which vanilla skips while the victim is already fleeing (and never reaches if the hit kills it).
 */
@Mixin(Animal.class)
public abstract class AnimalHurtAlarmMixin {
    @Inject(method = "actuallyHurt", at = @At("TAIL"))
    private void mite$alarmNeighbours(ServerLevel level, DamageSource source, float amount, CallbackInfo ci) {
        if (source.getEntity() instanceof LivingEntity attacker) {
            PanicAlarm.alertNearbyAnimals((Animal) (Object) this, attacker);
        }
    }
}
