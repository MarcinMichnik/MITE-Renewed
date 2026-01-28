package miterenewed;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static KeyBinding TOGGLE_SPRINT;
    public static KeyBinding ZOOM_KEY;

    public static void register() {
        TOGGLE_SPRINT = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.miterenewed.toggle_sprint",      // translation key
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_TAB,                // TAB key
                KeyBinding.Category.MOVEMENT
        ));

        ZOOM_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.yourmod.zoom",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                KeyBinding.Category.MOVEMENT
        ));
    }
}
