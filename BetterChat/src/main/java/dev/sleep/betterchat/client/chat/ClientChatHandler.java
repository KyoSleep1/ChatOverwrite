package dev.sleep.betterchat.client.chat;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ClientChatHandler {

    private static final List<Integer> PLAYER_MESSAGE_LIST = new ArrayList<>();

    @Getter @Setter
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
        return PLAYER_MESSAGE_LIST.contains(addedTime);
    }

    public static GuiMessage getLastFetchedMessage(List<GuiMessage> messageList){
        for(GuiMessage message : messageList){
            if(message.addedTime() != lastFetchedAddedTime){
                continue;
            }

            return message;
        }

        return null;
    }

    public static String getLegibleText(FormattedCharSequence charSequence) {
        StringBuilder stringBuilder = new StringBuilder();

        charSequence.accept((index, style, codePoints) -> {
            stringBuilder.appendCodePoint(codePoints);
            return true;
        });

        return stringBuilder.toString().strip();
    }
}
