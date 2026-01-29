package miterenewed;

import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final String MOD_ID = "mite-renewed";

    public static final ToolMaterial FLINT_TOOL_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL, // Which blocks it CANNOT break
            32,      // Durability
            2.0F,    // Mining Speed
            1.0F,    // Attack Damage
            5,       // Enchantability
            TagKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "flint")) // Repair item
    );

    public static Item FLINT_HATCHET;

    public static void registerModItems() {
        System.out.println("Registering Mod Items for " + MOD_ID);
        registerFlintHatchet();
    }

    private static void registerFlintHatchet() {
        Identifier id = Identifier.of(MOD_ID, "flint_hatchet");
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        AxeItem flintHatchet = new AxeItem(FLINT_TOOL_MATERIAL, 3.0F, -3.0F, new Item.Settings()
            .maxDamage(32)
            .useItemPrefixedTranslationKey()
            .registryKey(key));
        FLINT_HATCHET = Registry.register(Registries.ITEM, key, flintHatchet);
    }

}