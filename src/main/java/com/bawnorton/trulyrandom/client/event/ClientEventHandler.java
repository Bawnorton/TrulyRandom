package com.bawnorton.trulyrandom.client.event;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.keybind.KeybindManager;
import com.bawnorton.trulyrandom.client.screen.render.BlockStateGuiRenderer;
import com.bawnorton.trulyrandom.network.packet.serverbound.ServerboundHandshakePacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;

public class ClientEventHandler {
    public static void init() {
        registerKeybindEvents();
        registerWorldJoinEvent();
    }

    private static void registerKeybindEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(KeybindManager::runKeybindActions);
    }

    private static void registerWorldJoinEvent() {
        ClientPlayConnectionEvents.JOIN.register((_, sender, _) -> {
            sender.sendPacket(new ServerboundHandshakePacket());
            TrulyRandomClient.getLootBookController().reset();
        });

        PictureInPictureRendererRegistry.register(ctx -> new BlockStateGuiRenderer(ctx.bufferSource()));
    }
}
