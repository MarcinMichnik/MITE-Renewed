package miterenewed;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public class ModItems {

    public static final String MOD_ID = "mite-renewed";

    public static final ToolMaterial FLINT_TOOL_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL, // Which blocks it CANNOT break
            32,      // Durability
            2.0F,    // Mining Speed
            1.0F,    // Attack Damage
            5,       // Enchantability
            TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("minecraft", "flint")) // Repair item
    );

    public static Item FLINT_HATCHET;

    public static void registerModItems() {
        System.out.println("Registering Mod Items for " + MOD_ID);
        registerFlintHatchet();
    }

    private static void registerFlintHatchet() {
        Identifier id = Identifier.fromNamespaceAndPath(MOD_ID, "flint_hatchet");
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        AxeItem flintHatchet = new AxeItem(FLINT_TOOL_MATERIAL, 3.0F, -3.0F, new Item.Properties()
            .durability(32)
            .useItemDescriptionPrefix()
            .setId(key));
        FLINT_HATCHET = Registry.register(BuiltInRegistries.ITEM, key, flintHatchet);
    }

}