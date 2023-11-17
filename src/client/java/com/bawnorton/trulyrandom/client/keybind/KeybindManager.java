package com.bawnorton.trulyrandom.client.keybind;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.screen.TrulyRandomSettingsScreen;
import com.bawnorton.trulyrandom.network.packet.c2s.SyncRandomiserC2SPacket;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class KeybindManager {
    private static final List<ActionedKeybind> KEYBINDS = new ArrayList<>();
    public static final ActionedKeybind OPEN_RANDOMISER_GUI = registerKeybind("key.trulyrandom.open_randomiser_gui", GLFW.GLFW_KEY_G, client -> {
        client.setScreen(new TrulyRandomSettingsScreen(client.currentScreen, (modules, seed) -> client.getNetworkHandler().sendPacket(new SyncRandomiserC2SPacket(modules, seed))));
    });
    public static final ActionedKeybind RELOAD_CHUNKS = registerDevOnlyKeybind("key.trulyrandom.reload_chunks", GLFW.GLFW_KEY_KP_0, client -> {
        client.worldRenderer.reload();
    });
    public static final ActionedKeybind QUERY_HAND = registerDevOnlyKeybind("key.trulyrandom.query_hand", GLFW.GLFW_KEY_KP_1, client -> {
        Item handItem = client.player.getMainHandStack().getItem();
        ModelShuffler.Items items = (ModelShuffler.Items) client.getItemRenderer().getModels();
        TrulyRandom.LOGGER.info("Hand item: " + handItem + " (" + items.trulyrandom$getOriginalRandomisedMap().get(handItem) + ")");
        HitResult hitResult = client.crosshairTarget;
        if (hitResult instanceof BlockHitResult blockHitResult) {
            BlockState block = client.world.getBlockState(blockHitResult.getBlockPos());
            ModelShuffler.BlockStates blockStates = (ModelShuffler.BlockStates) client.getBlockRenderManager().getModels();
            TrulyRandom.LOGGER.info("Block: " + block + " (" + blockStates.trulyrandom$getOriginalRandomisedMap().get(block) + ")");
        }
    });
    public static final ActionedKeybind NEW_SEED = registerDevOnlyKeybind("key.trulyrandom.sync_randomiser", GLFW.GLFW_KEY_KP_2, client -> {
        client.getNetworkHandler().sendPacket(new SyncRandomiserC2SPacket(TrulyRandom.getRandomiser(client.getServer()).getModules(), TrulyRandomClient.getRandomiser().getLocalSeed() + 1));
    });

    public static void init() {
    }

    private static ActionedKeybind registerKeybind(String key, int code, KeybindCallback callback) {
        ActionedKeybind keybind = new ActionedKeybind(KeyBindingHelper.registerKeyBinding(new KeyBinding(
                key,
                InputUtil.Type.KEYSYM,
                code,
                "key.categories.trulyrandom"
        )), callback);
        KEYBINDS.add(keybind);
        return keybind;
    }

    private static ActionedKeybind registerDevOnlyKeybind(String key, int code, KeybindCallback callback) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            return registerKeybind(key, code, callback);
        }
        return null;
    }

    public static void runKeybindActions(MinecraftClient client) {
        KEYBINDS.forEach(keybind -> keybind.runAction(client));
    }

    @FunctionalInterface
    public interface KeybindCallback {
        void onKeybindPressed(MinecraftClient client);
    }

    public static class ActionedKeybind {
        private final KeyBinding keybind;
        private final KeybindCallback callback;

        public ActionedKeybind(KeyBinding keybind, KeybindCallback callback) {
            this.keybind = keybind;
            this.callback = callback;
        }

        public KeyBinding getKeybind() {
            return keybind;
        }

        public void runAction(MinecraftClient client) {
            while (keybind.wasPressed()) {
                callback.onKeybindPressed(client);
            }
        }

        public void invokeAction(MinecraftClient client) {
            callback.onKeybindPressed(client);
        }
    }
}
