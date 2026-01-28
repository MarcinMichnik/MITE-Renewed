package miterenewed.mixin.client;

import miterenewed.ZoomToggle;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Mouse.class)
public class MouseSensitivityMixin {

    @ModifyVariable(method = "updateMouse", at = @At("STORE"), ordinal = 2)
    private double scaleSensitivityX(double d) {
        // 'd' is the horizontal movement delta
        if (ZoomToggle.getZoomToggle().isZoomToggleEnabled()) {
            return d * 0.66;
        }
        return d;
    }

    @ModifyVariable(method = "updateMouse", at = @At("STORE"), ordinal = 3)
    private double scaleSensitivityY(double e) {
        // 'e' is the vertical movement delta
        if (ZoomToggle.getZoomToggle().isZoomToggleEnabled()) {
            return e * 0.66;
        }
        return e;
    }

}