package miterenewed.mixin.client;

import miterenewed.ZoomToggle;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractClientPlayer.class)
public class ZoomMixin {

    @ModifyVariable(method = "getFieldOfViewModifier", at = @At("STORE"), ordinal = 2)
    private float zoomInIfZoomToggleEnabled(float h) {
        if (ZoomToggle.getZoomToggle().isZoomToggleEnabled()) {
            return 500F;
        }
        return h;
    }

}