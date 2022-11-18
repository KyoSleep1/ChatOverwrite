package dev.sleep.betterchat;

import dev.sleep.betterchat.network.NetworkManager;
import net.fabricmc.api.ClientModInitializer;

public class MainClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        NetworkManager.registerClientPackets();
    }

}