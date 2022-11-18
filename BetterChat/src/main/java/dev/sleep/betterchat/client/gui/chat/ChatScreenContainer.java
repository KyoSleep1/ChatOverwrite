package dev.sleep.betterchat.client.gui.chat;

import dev.sleep.betterchat.client.chat.ClientChatHandler;
import dev.sleep.betterchat.client.gui.chat.widget.ChatButton;
import dev.sleep.betterchat.mixin.client.MinecraftAccessor;
import dev.sleep.betterchat.network.NetworkManager;
import lombok.Getter;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;

import java.util.List;

//This class works as a storage for the buttons
public class ChatScreenContainer {

    @Getter
    private static final ChatButton EditButton = new ChatButton("TEST 1", 1, 8, 7, 6, 2, 18,
            18, 18, 12, 12, (allMessagesList, visibleMessagesList, chatButton, messageIndex) -> {
        ChatScreenContainer.openChat(ClientChatHandler.getMessageTextAndFormat(allMessagesList));
    });

    @Getter
    private static final ChatButton DeleteButton = new ChatButton("TEST 2", 5, 8, 3, 6, 2, 2,
            19, 2, 11, 12, (allMessagesList, visibleMessagesList, chatButton, messageIndex) -> {
        ChatScreenContainer.deleteMessage(allMessagesList, visibleMessagesList, messageIndex);
    });


    private static void openChat(String messageToEdit) {
        ((MinecraftAccessor) Minecraft.getInstance()).openChat(messageToEdit);
    }

    private static void deleteMessage(List<GuiMessage> allMessagesList, List<GuiMessage.Line> visibleMessagesList, int messageIndex) {
        ((MinecraftAccessor) Minecraft.getInstance()).openChat("");

        ClientChatHandler.deleteMessageFromEverything(allMessagesList, visibleMessagesList, messageIndex);
        ClientPlayNetworking.send(NetworkManager.DELETE_MESSAGE_PACKET_ID, PacketByteBufs.create());
    }
}
