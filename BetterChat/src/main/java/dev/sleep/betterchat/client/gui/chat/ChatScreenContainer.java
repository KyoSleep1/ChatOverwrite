package dev.sleep.betterchat.client.gui.chat;

import dev.sleep.betterchat.client.chat.ClientChatHandler;
import dev.sleep.betterchat.client.gui.chat.widget.ChatButton;
import dev.sleep.betterchat.mixin.client.MinecraftAccessor;
import lombok.Getter;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

//This class works as a storage for the buttons
public class ChatScreenContainer {

    @Getter
    private static final ChatButton EditButton = new ChatButton("TEST 1", 1, 8, 7, 6, 2, 18,
            18, 18, 12, 12, (messageList, chatButton) -> {
        ChatScreenContainer.openChat(ChatScreenContainer.getMessageTextAndFormat(messageList));
    });

    @Getter
    private static final ChatButton DeleteButton = new ChatButton("TEST 2", 5, 8, 3, 6, 2, 2,
            19, 2, 11, 12, (messageList, chatButton) -> {
        ChatScreenContainer.openChat(ChatScreenContainer.getMessageTextAndFormat(messageList));
    });

    private static void openChat(String defaultText) {
        ((MinecraftAccessor) Minecraft.getInstance()).openChat(defaultText);
    }

    // Used to get the player message without the actual player's DisplayName (Extra steps are used to ensure chat plugins/mods compatibility)
    private static String getMessageTextAndFormat(List<GuiMessage> messageList) {
        GuiMessage selectedMessage = ClientChatHandler.getLastFetchedMessage(messageList);
        if (selectedMessage == null) {
            return "";
        }

        return ChatScreenContainer.getFormattedText(ClientChatHandler.getLegibleText(selectedMessage.content().
                getVisualOrderText()).split(" "), 0);
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
            return ChatScreenContainer.getFormattedText(splittedText, startIndex);
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
