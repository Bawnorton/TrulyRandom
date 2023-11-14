package com.bawnorton.trulyrandom.client.keybind;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class KeybindManager {
    public static final KeyBinding OPEN_RANDOMISER_GUI = registerKeybind("key.trulyrandom.open_randomiser_gui", GLFW.GLFW_KEY_R);
    public static final Optional<KeyBinding> RELOAD_CHUNKS = registerDevOnlyKeybind("key.trulyrandom.reload_chunks", GLFW.GLFW_KEY_KP_0);
    public static final Optional<KeyBinding> QUERY_HAND = registerDevOnlyKeybind("key.trulyrandom.query_hand", GLFW.GLFW_KEY_KP_1);

    public static void init() {
    }

    private static KeyBinding registerKeybind(String key, int code) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                key,
                InputUtil.Type.KEYSYM,
                code,
                "key.categories.trulyrandom"
        ));
    }

    private static Optional<KeyBinding> registerDevOnlyKeybind(String key, int code) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            return Optional.of(registerKeybind(key, code));
        }
        return Optional.empty();
    }
}
