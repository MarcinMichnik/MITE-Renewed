package miterenewed.mixin.client;

import miterenewed.SprintToggle;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public class SprintToggleMixin {

    @Redirect(method = "tickMovement",at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;setSprinting(Z)V"))
    private void preventSprintStop(ClientPlayerEntity instance, boolean sprinting) {
        // If the game wants to turn off sprinting (sprinting == false)
        // but our TAB toggle is on, we simply don't call the setter.
        if (SprintToggle.getSprintToggle().isToggleEnabled()) {
            instance.setSprinting(true);
        } else {
            instance.setSprinting(sprinting);
        }
    }

}