package miterenewed;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

public class ModKeyBindings {
    public static KeyMapping TOGGLE_SPRINT;
    public static KeyMapping ZOOM_KEY;

    public static void register() {
        TOGGLE_SPRINT = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.miterenewed.toggle_sprint",      // translation key
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_TAB,                // TAB key
                KeyMapping.Category.MOVEMENT
        ));

        ZOOM_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.miterenewed.zoom",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_Z,
                KeyMapping.Category.GAMEPLAY
        ));
    }
}
