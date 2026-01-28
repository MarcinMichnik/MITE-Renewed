package miterenewed.mixin;

import miterenewed.ProgressionHelpers;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public class HungerManagerMixin {
	@Shadow private int foodLevel;

	@Inject(method = "add", at = @At("HEAD"), cancellable = true)
	private void limitHunger(int food, float saturation, CallbackInfo ci) {
		if (!((Object) this instanceof ServerPlayerEntity player)) return;

		int maxFood = ProgressionHelpers.getMaxFoodLevel(player);
		if (this.foodLevel + food > maxFood) {
			this.foodLevel = maxFood;
			ci.cancel();
		}
	}

}