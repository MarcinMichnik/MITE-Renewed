package miterenewed.mixin.client;

import miterenewed.ZoomToggle;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractClientPlayerEntity.class)
public class ZoomMixin {

    @ModifyVariable(method = "getFovMultiplier", at = @At("STORE"), ordinal = 2)
    private float zoomInIfZoomToggleEnabled(float h) {
        if (ZoomToggle.getZoomToggle().isZoomToggleEnabled()) {
            return 500F;
        }
        return h;
    }

}