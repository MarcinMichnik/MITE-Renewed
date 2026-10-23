package miterenewed.mixin.item;

import miterenewed.ModConstants;
import net.minecraft.world.item.ToolMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ToolMaterial.class)
public class ToolMaterialDurabilityMixin {
    // Vanilla WOOD durability is 59
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 59))
    private static int miteWoodDurability(int original) {
        return ModConstants.WOODEN_TOOL_DURABILITY;
    }

    // Vanilla STONE durability is 131
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 131))
    private static int miteStoneDurability(int original) {
        return ModConstants.STONE_TOOL_DURABILITY;
    }

    // Vanilla COPPER durability is 190
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 190))
    private static int miteCopperDurability(int original) {
        return ModConstants.COPPER_TOOL_DURABILITY;
    }

    // Vanilla GOLD durability is 32
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 32))
    private static int miteGoldDurability(int original) {
        return ModConstants.GOLDEN_TOOL_DURABILITY;
    }

    // Vanilla IRON durability is 250
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 250))
    private static int miteIronDurability(int original) {
        return ModConstants.IRON_TOOL_DURABILITY;
    }

    // Vanilla DIAMOND durability is 1561
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 1561))
    private static int miteDiamondDurability(int original) {
        return ModConstants.DIAMOND_TOOL_DURABILITY;
    }

    // Vanilla NETHERITE durability is 2031
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 2031))
    private static int miteNetheriteDurability(int original) {
        return ModConstants.NETHERITE_TOOL_DURABILITY;
    }
}
