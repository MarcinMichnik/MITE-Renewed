package miterenewed.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MiteDamageTrackerMixin {
    @Unique private boolean nonPlayerDamageDetected = false;

    @Inject(method = "hurtServer", at = @At("HEAD"))
    private void mite$trackDamageSource(ServerLevel serverLevel, DamageSource source, float amount,
                                        CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof Monster)) return;

        Entity attacker = source.getEntity();
        Entity directSource = source.getDirectEntity();
        if (!(attacker instanceof Player) && !(directSource instanceof Player) && amount > 0.0f) {
            this.nonPlayerDamageDetected = true;
        }
    }

    @Inject(method = "getExperienceReward", at = @At("HEAD"), cancellable = true)
    private void mite$cancelUnfairExperience(ServerLevel level, Entity attacker, CallbackInfoReturnable<Integer> cir) {
        if (nonPlayerDamageDetected) {
            cir.setReturnValue(0);
        }
    }

}