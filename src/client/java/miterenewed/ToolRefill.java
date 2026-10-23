package miterenewed;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * When the held tool breaks, the next right click with the now empty hotbar slot selected pulls
 * a replacement out of the inventory: the same item if there is one, otherwise another tool of
 * the same kind (any pickaxe for a pickaxe, any sword for a sword...).
 */
public class ToolRefill {
    private static final List<TagKey<Item>> TOOL_KINDS = List.of(
        ItemTags.PICKAXES, ItemTags.AXES, ItemTags.SHOVELS, ItemTags.HOES, ItemTags.SWORDS, ItemTags.SPEARS
    );
    private static final int MAIN_INVENTORY_SIZE = 36; // hotbar 0-8, then the 27 storage slots
    private static final int HOTBAR_MENU_SLOT_OFFSET = 36; // hotbar slot i is menu slot 36 + i in the player inventory menu

    private static LocalPlayer brokenToolOwner;
    private static Item brokenTool;
    private static int brokenToolSlot = -1;

    public static void onMainHandToolBroken(LocalPlayer player, ItemStack tool) {
        brokenToolOwner = player;
        brokenTool = tool.getItem();
        brokenToolSlot = player.getInventory().getSelectedSlot();
    }

    /** Returns true if a replacement was moved into the selected slot, consuming the right click. */
    public static boolean tryRefill(Minecraft mc) {
        LocalPlayer player = mc.player;
        if (player == null || mc.gameMode == null || brokenTool == null || player != brokenToolOwner) {
            return false;
        }
        Inventory inventory = player.getInventory();
        int selected = inventory.getSelectedSlot();
        if (selected != brokenToolSlot) {
            return false;
        }
        if (!inventory.getItem(selected).isEmpty()) {
            // The slot was filled some other way; nothing to refill anymore
            brokenTool = null;
            return false;
        }

        int replacementSlot = findReplacement(inventory, selected);
        if (replacementSlot < 0) {
            return false;
        }
        int menuSlot = replacementSlot < Inventory.getSelectionSize()
            ? HOTBAR_MENU_SLOT_OFFSET + replacementSlot
            : replacementSlot;
        // Same as hovering the replacement in the inventory screen and pressing the hotbar number key
        mc.gameMode.handleContainerInput(player.inventoryMenu.containerId, menuSlot, selected, ContainerInput.SWAP, player);
        brokenTool = null;
        return true;
    }

    private static int findReplacement(Inventory inventory, int excludedSlot) {
        int sameKindSlot = -1;
        for (int i = 0; i < MAIN_INVENTORY_SIZE; i++) {
            if (i == excludedSlot) continue;
            ItemStack stack = inventory.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.is(brokenTool)) {
                return i;
            }
            if (sameKindSlot < 0 && isSameKind(stack)) {
                sameKindSlot = i;
            }
        }
        return sameKindSlot;
    }

    private static boolean isSameKind(ItemStack stack) {
        ItemStack broken = brokenTool.getDefaultInstance();
        for (TagKey<Item> kind : TOOL_KINDS) {
            if (broken.is(kind) && stack.is(kind)) {
                return true;
            }
        }
        return false;
    }
}
