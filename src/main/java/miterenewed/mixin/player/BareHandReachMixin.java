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
 * Shortens the player's reach towards entities and blocks (breaking and placing) when the main hand
 * holds no tool, weapon, stick or bone. Updated every tick on both sides, so the crosshair (client)
 * and validation (server) agree. Items with their own attack range (spears) keep their entity reach.
 */
@Mixin(Player.class)
public abstract class BareHandReachMixin {
    @Unique
    private static final Identifier BARE_HAND_REACH =
            Identifier.fromNamespaceAndPath(ModItems.MOD_ID, "bare_hand_reach");
    @Unique
    private static final Identifier BARE_HAND_BLOCK_REACH =
            Identifier.fromNamespaceAndPath(ModItems.MOD_ID, "bare_hand_block_reach");

    @Inject(method = "tick", at = @At("HEAD"))
    private void mite$updateBareHandReach(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        boolean shorten = !player.isCreative() && !isReachItem(player.getMainHandItem());
        updateModifier(player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE), BARE_HAND_REACH, shorten,
                ModConstants.BARE_HAND_ATTACK_RANGE - Player.DEFAULT_ENTITY_INTERACTION_RANGE);
        updateModifier(player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE), BARE_HAND_BLOCK_REACH, shorten,
                ModConstants.BARE_HAND_BLOCK_RANGE - Player.DEFAULT_BLOCK_INTERACTION_RANGE);
    }

    @Unique
    private static void updateModifier(AttributeInstance attribute, Identifier id, boolean apply, double amount) {
        if (attribute == null) {
            return;
        }
        if (apply && !attribute.hasModifier(id)) {
            attribute.addTransientModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE));
        } else if (!apply && attribute.hasModifier(id)) {
            attribute.removeModifier(id);
        }
    }

    @Unique
    private static boolean isReachItem(ItemStack stack) {
        return stack.has(DataComponents.TOOL) || stack.has(DataComponents.WEAPON)
                || stack.is(Items.STICK) || stack.is(Items.BONE);
    }
}
