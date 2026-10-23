package miterenewed.mixin.client.input;

import miterenewed.ToolRefill;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class ToolRefillMixin {
    @Shadow
    private int rightClickDelay;

    // A right click that refills a broken tool's slot does nothing else (no block or item use with the new tool)
    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    private void refillBrokenTool(CallbackInfo ci) {
        if (ToolRefill.tryRefill((Minecraft) (Object) this)) {
            this.rightClickDelay = 4; // as vanilla does, so holding right click doesn't use the tool right away
            ci.cancel();
        }
    }
}
