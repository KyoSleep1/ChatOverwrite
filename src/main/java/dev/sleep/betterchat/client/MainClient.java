package dev.sleep.betterchat.client;

import dev.sleep.betterchat.common.network.NetworkManager;
import dev.sleep.betterchat.server.event.EventHandler;
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
