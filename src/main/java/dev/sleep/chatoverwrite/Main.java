package dev.sleep.chatoverwrite;

import dev.sleep.chatoverwrite.common.network.NetworkManager;
import dev.sleep.chatoverwrite.server.event.EventHandler;
import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {

    @Getter
    private static final Logger Logger = LoggerFactory.getLogger(Reference.MODID);

    @Override
    public void onInitialize() {
        this.registerNetwork();
        this.registerEvents();
    }

    private void registerNetwork(){
        NetworkManager.registerServerPackets();
    }

    private void registerEvents(){
        EventHandler.registerAll();
    }
}
