package dev.sleep.betterchat.client;

import dev.sleep.betterchat.common.network.NetworkManager;
import net.fabricmc.api.ClientModInitializer;

public class MainClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        NetworkManager.registerClientPackets();
    }

}
