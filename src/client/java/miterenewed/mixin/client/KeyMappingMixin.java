package miterenewed.mixin.client;

import miterenewed.AutoMineManager;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin {
    @Shadow
    @Final
    private String name;

    @Inject(method = "isDown", at = @At("HEAD"), cancellable = true)
    private void forceAutoMineDown(CallbackInfoReturnable<Boolean> cir) {
        // "key.attack" is the internal translation key for Left Click
        if ("key.attack".equals(this.name) && AutoMineManager.isAutoMineActive()) {
            cir.setReturnValue(true);
        }
    }
}