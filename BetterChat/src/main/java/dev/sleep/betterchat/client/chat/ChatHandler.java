package dev.sleep.betterchat.client.chat;

import dev.sleep.betterchat.mixin.client.MinecraftAccessor;
import dev.sleep.betterchat.network.NetworkManager;
import dev.sleep.betterchat.network.packet.PacketMessageDeleted;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ChatHandler {

    private static final List<Integer> PLAYER_MESSAGE_LIST = new ArrayList<>();

    @Getter
    @Setter
    private static int lastFetchedAddedTime = -40000;

    public static void addToMessageList(Minecraft minecraft, UUID profileUUID) {
        LocalPlayer localPlayer = minecraft.player;
        if (!profileUUID.equals(Objects.requireNonNull(localPlayer).getUUID())) {
            return;
        }

        PLAYER_MESSAGE_LIST.add(minecraft.gui.getGuiTicks());
    }

    public static String getTextFromEditedButton(List<GuiMessage> allMessagesList) {
        GuiMessage selectedMessage = ChatHandler.getLastFetchedMessage(allMessagesList);
        if (selectedMessage == null) {
            return "";
        }

        return MessageHandler.getMessageTextAndFormat(selectedMessage);
    }

    public static void deleteMessageFromEverything(List<GuiMessage> allMessagesList, List<GuiMessage.Line> visibleMessageList, int messageIndex) {
        ChatHandler.deleteMessageFromPlayerMessageList(allMessagesList, messageIndex);
        allMessagesList.remove(messageIndex);
        visibleMessageList.remove(messageIndex);
    }

    private static void deleteMessageFromPlayerMessageList(List<GuiMessage> allMessagesList, int messageIndex) {
        GuiMessage messageToDelete = ChatHandler.getGuiMessageByIndex(allMessagesList, messageIndex);
        for (int iteratorIndex = 0; iteratorIndex < PLAYER_MESSAGE_LIST.size(); iteratorIndex++) {
            int storedMessageAddedTime = PLAYER_MESSAGE_LIST.get(iteratorIndex);

            if (messageToDelete.addedTime() != storedMessageAddedTime) {
                continue;
            }

            PLAYER_MESSAGE_LIST.remove(iteratorIndex);
            break;
        }
    }

    public static GuiMessage getGuiMessageByIndex(List<GuiMessage> visibleMessageList, int messageIndex) {
        return visibleMessageList.get(messageIndex);
    }

    public static void clearMessageList() {
        PLAYER_MESSAGE_LIST.clear();
    }

    public static boolean isMessageOwner(int addedTime) {
        return PLAYER_MESSAGE_LIST.contains(addedTime);
    }

    public static GuiMessage getLastFetchedMessage(List<GuiMessage> messageList) {
        for (GuiMessage message : messageList) {
            if (message.addedTime() != lastFetchedAddedTime) {
                continue;
            }

            return message;
        }

        return null;
    }

    public static void openChat(String messageToEdit) {
        ((MinecraftAccessor) Minecraft.getInstance()).openChat(messageToEdit);
    }

    public static void deleteMessage(List<GuiMessage> allMessagesList, List<GuiMessage.Line> visibleMessagesList, int messageIndex) {
        ((MinecraftAccessor) Minecraft.getInstance()).openChat("");

        ChatHandler.deleteMessageFromEverything(allMessagesList, visibleMessagesList, messageIndex);
        ClientPlayNetworking.send(NetworkManager.DELETE_MESSAGE_PACKET_ID, PacketMessageDeleted.createWritedBuf());
    }
}
