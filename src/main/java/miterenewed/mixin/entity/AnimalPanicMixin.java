package miterenewed.mixin.entity;

import miterenewed.ModConstants;
import miterenewed.PanicAlarm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes panicking animals (cows, pigs, sheep, chickens...) much harder to catch:
 * <ul>
 *   <li>they run faster and flee <em>away</em> from whoever hit them instead of in a random direction,</li>
 *   <li>they keep fleeing for a while after the hit, and longer while the attacker stays close,</li>
 *   <li>a hit raises the alarm for the whole herd (same mob type nearby), which scatters too.</li>
 * </ul>
 */
@Mixin(PanicGoal.class)
public abstract class AnimalPanicMixin implements PanicAlarm {
    @Unique private static final int NOT_ALARMED = -1_000_000;
    @Unique private static final int FLEE_DISTANCE = 16;
    @Unique private static final int FLEE_HEIGHT = 7;
    @Unique private static final double REPATH_DISTANCE_SQR = 6.0 * 6.0;

    @Shadow @Final protected PathfinderMob mob;
    @Shadow @Final @Mutable protected double speedModifier;
    @Shadow protected double posX;
    @Shadow protected double posY;
    @Shadow protected double posZ;

    @Shadow protected abstract boolean findRandomPosition();

    @Unique @Nullable private LivingEntity threat;
    @Unique private int alarmTick = NOT_ALARMED;
    @Unique private int lastHerdAlertTick = NOT_ALARMED;

    // The other constructors delegate here, so the boost is applied exactly once
    @Inject(method = "<init>(Lnet/minecraft/world/entity/PathfinderMob;DLjava/util/function/Function;)V", at = @At("RETURN"))
    private void mite$boostPanicSpeed(CallbackInfo ci) {
        this.speedModifier *= ModConstants.PANIC_SPEED_MULTIPLIER;
    }

    @Override
    public void mite$alarm(@Nullable LivingEntity threat) {
        this.threat = threat;
        this.alarmTick = mob.tickCount;
    }

    @Inject(method = "shouldPanic", at = @At("RETURN"), cancellable = true)
    private void mite$panicWhileAlarmed(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            // Vanilla says we were just hurt: remember who did it and warn the herd
            LivingEntity attacker = mob.getLastHurtByMob();
            mite$alarm(attacker);
            if (attacker != null && mob.tickCount - lastHerdAlertTick >= 20) {
                lastHerdAlertTick = mob.tickCount;
                alertHerd(attacker);
            }
        } else if (isAlarmed()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "findRandomPosition", at = @At("HEAD"), cancellable = true)
    private void mite$fleeAwayFromThreat(CallbackInfoReturnable<Boolean> cir) {
        if (threat != null && threat.isAlive()) {
            Vec3 away = DefaultRandomPos.getPosAway(mob, FLEE_DISTANCE, FLEE_HEIGHT, threat.position());
            if (away != null) {
                posX = away.x;
                posY = away.y;
                posZ = away.z;
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "canContinueToUse", at = @At("HEAD"), cancellable = true)
    private void mite$keepFleeing(CallbackInfoReturnable<Boolean> cir) {
        if (!isAlarmed()) {
            return; // vanilla: stop once the current path is done
        }
        PathNavigation navigation = mob.getNavigation();
        boolean threatClose = threat != null && threat.isAlive() && mob.distanceToSqr(threat) < REPATH_DISTANCE_SQR;
        // New escape route when the path runs out, or regularly while the attacker is on our heels
        if ((navigation.isDone() || (threatClose && mob.tickCount % 10 == 0)) && findRandomPosition()) {
            navigation.moveTo(posX, posY, posZ, speedModifier);
        }
        cir.setReturnValue(true);
    }

    @Unique
    private boolean isAlarmed() {
        int sinceAlarm = mob.tickCount - alarmTick;
        if (sinceAlarm < ModConstants.PANIC_DURATION_TICKS) {
            return true;
        }
        double radius = ModConstants.PANIC_THREAT_RADIUS;
        return threat != null && threat.isAlive()
                && sinceAlarm < ModConstants.PANIC_MAX_DURATION_TICKS
                && mob.distanceToSqr(threat) < radius * radius;
    }

    @Unique
    private void alertHerd(LivingEntity attacker) {
        var area = mob.getBoundingBox().inflate(ModConstants.HERD_ALERT_RADIUS);
        for (PathfinderMob other : mob.level().getEntitiesOfClass(PathfinderMob.class, area,
                e -> e != mob && e.getType() == mob.getType() && e.isAlive())) {
            for (WrappedGoal goal : ((MobGoalsAccessor) other).mite$getGoalSelector().getAvailableGoals()) {
                if (goal.getGoal() instanceof PanicAlarm alarm) {
                    alarm.mite$alarm(attacker);
                }
            }
        }
    }
}
