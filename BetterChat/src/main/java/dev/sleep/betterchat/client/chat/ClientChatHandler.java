package dev.sleep.betterchat.client.chat;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.*;

public class ClientChatHandler {

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

    public static void deleteMessageFromEverything(List<GuiMessage> allMessagesList, List<GuiMessage.Line> visibleMessageList, int messageIndex) {
        ClientChatHandler.deleteMessageFromPlayerMessageList(allMessagesList, messageIndex);
        allMessagesList.remove(messageIndex);
        visibleMessageList.remove(messageIndex);
    }

    private static void deleteMessageFromPlayerMessageList(List<GuiMessage> allMessagesList, int messageIndex) {
        GuiMessage messageToDelete = ClientChatHandler.getGuiMessageByIndex(allMessagesList, messageIndex);
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

    // Used to get the player message without the actual player's DisplayName (Extra steps are used to ensure chat plugins/mods compatibility)
    public static String getMessageTextAndFormat(List<GuiMessage> messageList) {
        GuiMessage selectedMessage = ClientChatHandler.getLastFetchedMessage(messageList);
        if (selectedMessage == null) {
            return "";
        }

        return ClientChatHandler.getFormattedText(ClientChatHandler.getLegibleText(selectedMessage.content().
                getVisualOrderText()).split(" "), 0);
    }

    /**
     * Used to get Legible text from codePoints
     **/
    private static String getLegibleText(FormattedCharSequence charSequence) {
        StringBuilder stringBuilder = new StringBuilder();

        charSequence.accept((index, style, codePoints) -> {
            stringBuilder.appendCodePoint(codePoints);
            return true;
        });

        return stringBuilder.toString().strip();
    }

    /* Used to get the player message without the actual player's DisplayName
     * Extra steps are used to ensure compatibility with plugins/mods that change the chat structure
     * Example:
     *      Original: <Player468> Hi!
     *      Modified: > Player468 - Hi!
     */
    private static String getFormattedText(String[] splittedText, int startIndex) {
        Component playerDisplayName = Objects.requireNonNull(Minecraft.getInstance().player).getDisplayName();
        String messageLine = splittedText[startIndex];

        if (!messageLine.contains(playerDisplayName.getString())) {
            startIndex++;
            return ClientChatHandler.getFormattedText(splittedText, startIndex);
        }

        startIndex++;
        return buildMessage(splittedText, startIndex);
    }

    private static String buildMessage(String[] splittedText, int startIndex) {
        StringJoiner joiner = new StringJoiner(" ");

        for (int i = startIndex; i < splittedText.length; i++) {
            joiner.add(splittedText[i]);
        }

        return joiner.toString();
    }
}
