package miterenewed;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static KeyMapping TOGGLE_SPRINT;
    public static KeyMapping ZOOM_KEY;

    public static void register() {
        TOGGLE_SPRINT = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.miterenewed.toggle_sprint",      // translation key
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_TAB,                // TAB key
                KeyMapping.Category.MOVEMENT
        ));

        ZOOM_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.yourmod.zoom",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                KeyMapping.Category.MOVEMENT
        ));
    }
}
