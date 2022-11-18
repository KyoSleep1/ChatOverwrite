package dev.sleep.betterchat.client.gui.chat;

import dev.sleep.betterchat.client.chat.ClientChatHandler;
import dev.sleep.betterchat.client.gui.chat.widget.ChatButton;
import dev.sleep.betterchat.mixin.client.MinecraftAccessor;
import lombok.Getter;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;

import java.util.List;

//This class works as a storage for the buttons
public class ChatScreenContainer {

    @Getter
    private static final ChatButton EditButton = new ChatButton("TEST 1", 1, 8, 7, 6, 2, 18,
            18, 18, 12, 12, (messageList, chatButton) -> {
        openChat(ChatScreenContainer.getMessageText(messageList));
    });

    @Getter
    private static final ChatButton DeleteButton = new ChatButton("TEST 2", 5, 8, 3, 6, 2, 2,
            19, 2, 11, 12, (messageList, chatButton) -> {
        openChat(ChatScreenContainer.getMessageText(messageList));
    });

    private static void openChat(String defaultText) {
        ((MinecraftAccessor) Minecraft.getInstance()).openChat(defaultText);
    }

    private static String getMessageText(List<GuiMessage> messageList) {
        GuiMessage selectedMessage = ClientChatHandler.getLastFetchedMessage(messageList);
        if (selectedMessage == null) {
            return "";
        }

        return ClientChatHandler.getLegibleText(selectedMessage.content().getVisualOrderText());
    }
}
