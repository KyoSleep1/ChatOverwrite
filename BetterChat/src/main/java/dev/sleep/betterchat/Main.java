package dev.sleep.betterchat;

import dev.sleep.betterchat.common.network.NetworkManager;
import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {

    @Getter
    private static final Logger Logger = LoggerFactory.getLogger(Reference.MODID);

    @Override
    public void onInitialize() {
        NetworkManager.registerServerPackets();
    }
}
