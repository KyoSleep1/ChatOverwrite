package dev.sleep.betterchat.client.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ClientChatHandler {

    private static final List<Integer> PLAYER_MESSAGE_LIST = new ArrayList<>();
    private static int lastFetchedAddedTime = -40000;

    public static void addToMessageList(Minecraft minecraft, UUID profileUUID) {
        LocalPlayer localPlayer = minecraft.player;
        if (!profileUUID.equals(Objects.requireNonNull(localPlayer).getUUID())) {
            return;
        }

        PLAYER_MESSAGE_LIST.add(minecraft.gui.getGuiTicks());
    }

    public static void reset(boolean clearChatHistory) {
        if(clearChatHistory){
            PLAYER_MESSAGE_LIST.clear();
        }

        lastFetchedAddedTime = -40000;
    }

    public static boolean isMessageOwner(int addedTime) {
        lastFetchedAddedTime = addedTime;
        return PLAYER_MESSAGE_LIST.contains(addedTime);
    }
}
