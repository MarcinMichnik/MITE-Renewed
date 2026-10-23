package miterenewed.mixin.client.entity;

import miterenewed.ToolRefill;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The server tells the client an equipped item broke (entity event) before it syncs the emptied
 * slot, so the broken tool is still in hand here.
 */
@Mixin(LivingEntity.class)
public abstract class ToolBreakTrackerMixin {
    @Inject(method = "breakItem", at = @At("HEAD"))
    private void trackBrokenTool(ItemStack stack, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if ((Object) this == player && !stack.isEmpty() && stack == player.getMainHandItem()) {
            ToolRefill.onMainHandToolBroken(player, stack);
        }
    }
}
