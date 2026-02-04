package miterenewed.mixin;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Mixin(PanicGoal.class)
public abstract class LongPanicMixin {
    @Unique private long ticksSinceStartingPanic = 241;
    @Unique private long ticksSinceGettingHit = 241;
    @Shadow @Final protected PathfinderMob mob;
    @Shadow @Final protected double speedModifier;

    @Inject(method = "canContinueToUse", at = @At("HEAD"), cancellable = true)
    private void makePanicLonger(CallbackInfoReturnable<Boolean> cir) {
        if (mob.hurtTime > 8) { // hurtTime is set to 10 when hit and goes down to 0 with each tick
            ticksSinceStartingPanic = 0;
            ticksSinceGettingHit = 0;
        } else {
            ticksSinceGettingHit++;
            ticksSinceStartingPanic++;
        }
        if (ticksSinceStartingPanic > 200) {
            cir.setReturnValue(false);
            return;
        }
        if (ticksSinceStartingPanic == 0 || ticksSinceStartingPanic % 4 == 0) {
            ThreadLocalRandom rand = ThreadLocalRandom.current();
            int distance = 8;
            int xModifier = rand.nextBoolean() ? distance : -distance;
            int zModifier = rand.nextBoolean() ? distance : -distance;
            double targetX = mob.getX() + xModifier;
            double targetZ = mob.getZ() + zModifier;
            this.mob.getNavigation().moveTo(targetX, mob.getY(), targetZ, this.speedModifier);
        }
        cir.setReturnValue(true);
    }

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void onCanUse(CallbackInfoReturnable<Boolean> cir) {
        ticksSinceStartingPanic++;
        ticksSinceGettingHit++;
    }


    @Inject(method = "start", at = @At("HEAD"), cancellable = true)
    private void onStart(CallbackInfo ci) {
        ticksSinceStartingPanic = 0;
    }

    @Inject(method = "shouldPanic", at = @At("HEAD"), cancellable = true)
    private void forcePanic(CallbackInfoReturnable<Boolean> cir) {
        if (this.mob.isPanicking()) return;

        if (this.mob.hurtTime > 8) {
            this.ticksSinceStartingPanic = 0;
            this.ticksSinceGettingHit = 0;
            cir.setReturnValue(true);
            return;
        }

        if (this.ticksSinceStartingPanic > 200 && this.ticksSinceStartingPanic <= 240
                && this.ticksSinceGettingHit > 200) return;

        AABB area = this.mob.getBoundingBox().inflate(4.0D, 4.0D, 4.0D);
        List<PathfinderMob> neighbors = this.mob.level().getEntitiesOfClass(
                PathfinderMob.class,
                area,
                entity -> entity != this.mob // Don't alert yourself
        );
        for (PathfinderMob neighbor : neighbors) {
            if (neighbor.isPanicking()) {
                cir.setReturnValue(true);
                ticksSinceStartingPanic = 0;
                return;
            }
        }
    }

}