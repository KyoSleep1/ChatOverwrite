package dev.sleep.betterchat.client.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class ClientChatHandler {

    private static final HashMap<String, UUID> MessageOwnerList = new HashMap<>();

    public static void addToOwnerList(String key, UUID profileUUID) {
        MessageOwnerList.putIfAbsent(key, profileUUID);
    }

    public static boolean isMessageOwner(String legibleText) {
        AbstractClientPlayer clientPlayer = Minecraft.getInstance().player;
        UUID messageOwnerUUID = MessageOwnerList.get(legibleText);

        if(messageOwnerUUID == null) {
            return false;
        }

        return messageOwnerUUID.equals(Objects.requireNonNull(clientPlayer).getUUID());
    }
}
