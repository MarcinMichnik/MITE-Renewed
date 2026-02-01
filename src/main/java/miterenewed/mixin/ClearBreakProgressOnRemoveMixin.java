package miterenewed.mixin;

import miterenewed.DiggingMob;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class ClearBreakProgressOnRemoveMixin {

    @Inject(method = "remove", at = @At("HEAD"))
    private void clearBlockBreakOnRemove(Entity.RemovalReason reason, CallbackInfo ci) {
        if ((Object)this instanceof DiggingMob diggingMob) {
            BlockPos target = diggingMob.getDiggingTarget();
            if (target != null && ((Entity)(Object)this).level() instanceof ServerLevel level) {
                level.destroyBlockProgress(((Entity)(Object)this).getId(), target, -1);
            }
        }
    }
}