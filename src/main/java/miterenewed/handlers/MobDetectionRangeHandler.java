package miterenewed.handlers;

import miterenewed.ModConstants;
import miterenewed.ModItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;

/**
 * Hostile mobs notice players from further away by scaling their follow range,
 * which is the distance their targeting goals search for players.
 */
public class MobDetectionRangeHandler {
    private static final Identifier MODIFIER_ID =
            Identifier.fromNamespaceAndPath(ModItems.MOD_ID, "detection_range");

    public static void init() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (!(entity instanceof Mob mob) || !(entity instanceof Enemy)) {
                return;
            }
            AttributeInstance followRange = mob.getAttribute(Attributes.FOLLOW_RANGE);
            // Transient: not saved with the mob, re-applied on every load so it never stacks
            if (followRange != null && !followRange.hasModifier(MODIFIER_ID)) {
                followRange.addTransientModifier(new AttributeModifier(MODIFIER_ID,
                        ModConstants.MOB_DETECTION_RANGE_MULTIPLIER - 1.0,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        });
    }
}
