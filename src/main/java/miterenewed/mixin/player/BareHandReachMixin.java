package miterenewed.mixin.player;

import miterenewed.ModConstants;
import miterenewed.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Shortens the player's reach towards entities when the main hand holds no tool, weapon or stick.
 * Updated every tick on both sides, so the crosshair (client) and hit validation (server) agree.
 * Items with their own attack range (spears) are unaffected; block reach is unaffected.
 */
@Mixin(Player.class)
public abstract class BareHandReachMixin {
    @Unique
    private static final Identifier BARE_HAND_REACH =
            Identifier.fromNamespaceAndPath(ModItems.MOD_ID, "bare_hand_reach");

    @Inject(method = "tick", at = @At("HEAD"))
    private void mite$updateBareHandReach(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        AttributeInstance reach = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (reach == null) {
            return;
        }

        boolean shorten = !player.isCreative() && !isReachItem(player.getMainHandItem());
        if (shorten && !reach.hasModifier(BARE_HAND_REACH)) {
            reach.addTransientModifier(new AttributeModifier(BARE_HAND_REACH,
                    ModConstants.BARE_HAND_ATTACK_RANGE - Player.DEFAULT_ENTITY_INTERACTION_RANGE,
                    AttributeModifier.Operation.ADD_VALUE));
        } else if (!shorten && reach.hasModifier(BARE_HAND_REACH)) {
            reach.removeModifier(BARE_HAND_REACH);
        }
    }

    @Unique
    private static boolean isReachItem(ItemStack stack) {
        return stack.has(DataComponents.TOOL) || stack.has(DataComponents.WEAPON) || stack.is(Items.STICK);
    }
}
