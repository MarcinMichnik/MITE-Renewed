package miterenewed.mixin.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MiteDamageTrackerMixin {
    @Unique private boolean nonPlayerDamageDetected = false;

    @Inject(method = "hurtServer", at = @At("HEAD"))
    private void mite$trackDamageSource(ServerLevel serverLevel, DamageSource source, float amount,
                                        CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        // Bosses (the Wither here; the ender dragon is not a Monster) keep their vanilla XP and drops
        if (!(entity instanceof Monster) || entity instanceof WitherBoss) return;

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

    // Same rule for loot: no loot table or equipment drops unless the player did all the damage
    @Inject(method = "shouldDropLoot", at = @At("HEAD"), cancellable = true)
    private void mite$cancelUnfairLoot(ServerLevel level, CallbackInfoReturnable<Boolean> cir) {
        if (nonPlayerDamageDetected) {
            cir.setReturnValue(false);
        }
    }

    // ...but items a mob picked up (e.g. the player's lost gear) still drop
    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
    private void mite$keepPickedUpItems(ServerLevel level, DamageSource source, CallbackInfo ci) {
        if (nonPlayerDamageDetected && (Object) this instanceof Mob mob) {
            mob.dropPreservedEquipment(level);
        }
    }

}