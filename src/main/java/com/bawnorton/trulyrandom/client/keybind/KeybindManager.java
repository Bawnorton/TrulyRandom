package com.bawnorton.trulyrandom.client.keybind;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.network.ClientNetworking;
import com.bawnorton.trulyrandom.client.screen.TrulyRandomSettingsScreen;
import com.bawnorton.trulyrandom.network.packet.serverbound.ServerboundRequestServerRandomiserPacket;
import com.bawnorton.trulyrandom.network.packet.serverbound.ServerboundSetServerRandomiserPacket;
import com.bawnorton.trulyrandom.network.packet.clientbound.ClientboundSetClientRandomiserPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.List;

public class KeybindManager {
    private static final List<ActionedKeybind> KEYBINDS = new ArrayList<>();
    private static final KeyMapping.Category KEY_CATEGORY = new KeyMapping.Category(TrulyRandom.id("category"));

    public static final ActionedKeybind OPEN_RANDOMISER_GUI = registerKeybind("key.trulyrandom.open_randomiser_gui", GLFW.GLFW_KEY_G, minecraft -> {
        ClientNetworking.registerRecievedCallback(ClientboundSetClientRandomiserPacket.TYPE, () -> minecraft.setScreen(new TrulyRandomSettingsScreen(
                minecraft.screen,
                TrulyRandomClient.getRandomiser().getModules().copy(),
                (modules) -> ClientPlayNetworking.send(new ServerboundSetServerRandomiserPacket(modules))
        )));
        ClientPlayNetworking.send(ServerboundRequestServerRandomiserPacket.INSTANCE);
    });
    public static final ActionedKeybind RELOAD_CHUNKS = registerDevOnlyKeybind("key.trulyrandom.reload_chunks", GLFW.GLFW_KEY_KP_0, minecraft -> minecraft.levelRenderer.allChanged());
    public static final ActionedKeybind QUERY_HAND = registerDevOnlyKeybind("key.trulyrandom.query_hand", GLFW.GLFW_KEY_KP_1, minecraft -> {
        Item handItem = minecraft.player.getMainHandItem().getItem();
        ModelShuffler.Items items = (ModelShuffler.Items) minecraft.getModelManager();
        TrulyRandom.LOGGER.info("Hand item: {} ({})", handItem, items.trulyrandom$getRedirectMap().get(handItem));
        HitResult hitResult = minecraft.hitResult;
        if (hitResult instanceof BlockHitResult blockHitResult) {
            BlockState block = minecraft.level.getBlockState(blockHitResult.getBlockPos());
            ModelShuffler.BlockStates blockStates = (ModelShuffler.BlockStates) minecraft.getModelManager().getBlockModelSet();
            TrulyRandom.LOGGER.info("Block: {} ({})", block, blockStates.trulyrandom$getRedirectMap().get(block));
        }
    });

    public static void init() {
    }

    private static ActionedKeybind registerKeybind(String key, int code, KeybindCallback callback) {
        ActionedKeybind keybind = new ActionedKeybind(KeyMappingHelper.registerKeyMapping(new KeyMapping(
                key,
                InputConstants.Type.KEYSYM,
                code,
                KEY_CATEGORY
        )), callback);
        KEYBINDS.add(keybind);
        return keybind;
    }

    private static @Nullable ActionedKeybind registerDevOnlyKeybind(String key, int code, KeybindCallback callback) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            return registerKeybind(key, code, callback);
        }
        return null;
    }

    public static void runKeybindActions(Minecraft minecraft) {
        KEYBINDS.forEach(keybind -> keybind.runIfPressed(minecraft));
    }

    @FunctionalInterface
    public interface KeybindCallback {
        void onKeybindPressed(Minecraft minecraft);
    }

    public static class ActionedKeybind {
        private final KeyMapping keybind;
        private final KeybindCallback callback;

        public ActionedKeybind(KeyMapping keybind, KeybindCallback callback) {
            this.keybind = keybind;
            this.callback = callback;
        }

        public KeyMapping getKeybind() {
            return keybind;
        }

        public void runIfPressed(Minecraft minecraft) {
            while (keybind.consumeClick()) {
                run(minecraft);
            }
        }

        public void run(Minecraft minecraft) {
            callback.onKeybindPressed(minecraft);
        }
    }
}
