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
    @Unique private int mite$totalPanicTicks;
    @Shadow @Final protected PathfinderMob mob;
    @Shadow @Final protected double speedModifier;

    @Inject(method = "canContinueToUse", at = @At("HEAD"), cancellable = true)
    private void makePanicLonger(CallbackInfoReturnable<Boolean> cir) {
        if (mob.hurtTime > 0) {
            mite$totalPanicTicks = 0;
        }
        mite$totalPanicTicks++;
        if (mite$totalPanicTicks > 200) {
            mite$totalPanicTicks = 0;
            cir.setReturnValue(false);
            return;
        }
        if (mite$totalPanicTicks < 2 || mite$totalPanicTicks % 4 == 0) {
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

    @Inject(method = "stop", at = @At("TAIL"))
    private void onStop(CallbackInfo ci) {
        this.mite$totalPanicTicks = 0;
    }

    @Inject(method = "shouldPanic", at = @At("HEAD"), cancellable = true)
    private void mite$allowCustomPanic(CallbackInfoReturnable<Boolean> cir) {
        if (this.mob.isPanicking()) { return; }
        AABB area = this.mob.getBoundingBox().inflate(15.0D, 5.0D, 15.0D);
        List<PathfinderMob> neighbors = this.mob.level().getEntitiesOfClass(
                PathfinderMob.class,
                area,
                entity -> entity != this.mob // Don't alert yourself
        );
        for (PathfinderMob neighbor : neighbors) {
            if (neighbor.isPanicking()) {
                cir.setReturnValue(true);
            }
        }
    }

}