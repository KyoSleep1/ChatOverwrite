package dev.sleep.chatoverwrite.client;

import dev.sleep.chatoverwrite.common.network.NetworkManager;
import dev.sleep.chatoverwrite.server.event.EventHandler;
import net.fabricmc.api.ClientModInitializer;

public class MainClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        this.registerNetwork();
        this.registerEvents();
    }

    private void registerNetwork() {
        NetworkManager.registerClientPackets();
    }

    private void registerEvents() {
        EventHandler.registerAll();
    }

}
