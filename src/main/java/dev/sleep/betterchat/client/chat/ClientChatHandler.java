package dev.sleep.betterchat.client.chat;

import com.google.common.collect.HashBasedTable;
import dev.sleep.betterchat.common.chat.EditableChatMessage;
import dev.sleep.betterchat.common.chat.MessageHandler;
import dev.sleep.betterchat.common.network.NetworkManager;
import dev.sleep.betterchat.common.network.packet.PacketMessageDeleted;
import dev.sleep.betterchat.mixin.client.MinecraftAccessor;
import dev.sleep.betterchat.mixin.client.chat.ChatComponentAccessor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.PlayerChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ClientChatHandler {

    private static final HashBasedTable<UUID, Integer, EditableChatMessage> PLAYERS_MESSAGES_LIST = HashBasedTable.create();

    public static void addToEditableMessageList(PlayerChatMessage chatMessage, int addedTime) {
        UUID signerUUID = chatMessage.signer().profileId();
        PLAYERS_MESSAGES_LIST.put(signerUUID, addedTime, new EditableChatMessage(chatMessage, addedTime));
    }

    public static void clearMessageList() {
        PLAYERS_MESSAGES_LIST.clear();
    }

    public static boolean isMessageOwner(int addedTime) {
        EditableChatMessage chatMessage = ClientChatHandler.getEditableMessageByAddedTime(addedTime);
        if (chatMessage == null) {
            return false;
        }

        UUID playerUUID = chatMessage.getChatMessage().signer().profileId();
        return Objects.requireNonNull(Minecraft.getInstance().player).getUUID() == playerUUID;
    }

    private static EditableChatMessage getEditableMessageByAddedTime(int addedTime) {
        for (EditableChatMessage editableChatMessage : PLAYERS_MESSAGES_LIST.values()) {
            if (editableChatMessage.getAddedTime() != addedTime) {
                continue;
            }

            return editableChatMessage;
        }

        return null;
    }

    public static void editMessage(GuiMessage.Line lineMessage) {
        String textFromEditedButton = MessageHandler.getFormattedContentText(lineMessage);
        ClientChatHandler.openChat(textFromEditedButton);
    }

    public static void deleteMessage(GuiMessage.Line lineMessage) {
        ClientChatHandler.openChat("");
        ClientChatHandler.removeFromAllLists(lineMessage);
        ClientPlayNetworking.send(NetworkManager.DELETE_MESSAGE_PACKET_ID, PacketMessageDeleted.createWritedBuf());
    }

    public static void removeFromAllLists(GuiMessage.Line lineMessage) {
        ClientChatHandler.removeFromPlayersMessagesList(lineMessage);
        ClientChatHandler.removeFromAllMessagesList(lineMessage);
        ClientChatHandler.removeFromTrimMessagesList(lineMessage);
    }

    private static void removeFromPlayersMessagesList(GuiMessage.Line lineMessage) {
        EditableChatMessage editableChatMessage = ClientChatHandler.getEMByGuiMessage(lineMessage);

        UUID senderUUID = editableChatMessage.getChatMessage().signer().profileId();
        int addedTime = editableChatMessage.getAddedTime();

        PLAYERS_MESSAGES_LIST.remove(senderUUID, addedTime);
    }

    private static void removeFromAllMessagesList(GuiMessage.Line lineMessage) {
        GuiMessage message = MessageHandler.getMessageFromLine(lineMessage);
        ((ChatComponentAccessor) Minecraft.getInstance().gui.getChat()).getAllMessagesList().remove(message);
    }

    private static void removeFromTrimMessagesList(GuiMessage.Line lineMessage) {
        List<GuiMessage.Line> trimmedMessagesList = ((ChatComponentAccessor) Minecraft.getInstance().gui.getChat()).getTrimmedMessagesList();
        List<GuiMessage.Line> messageLinesToDelete = new ArrayList<>();

        for (GuiMessage.Line fetchedLineMessage : trimmedMessagesList) {
            if (fetchedLineMessage == null || fetchedLineMessage.addedTime() != lineMessage.addedTime()) {
                continue;
            }

            messageLinesToDelete.add(fetchedLineMessage);
        }

        for (GuiMessage.Line line : messageLinesToDelete) {
            trimmedMessagesList.remove(line);
        }
    }

    /**
     * The actual method name is getEditableMessageByGuiMessage.
     * I don't like large stuff... ( ͡° ͜ʖ ͡°)
     * no.
     * <p>
     * Be careful when using this method because it doesn't return null, instead it returns an empty object
     */
    public static EditableChatMessage getEMByGuiMessage(GuiMessage.Line lineMessage) {
        GuiMessage message = MessageHandler.getMessageFromLine(lineMessage);
        if (message == null) {
            return new EditableChatMessage(null, 0);
        }

        for (EditableChatMessage editableChatMessage : PLAYERS_MESSAGES_LIST.values()) {
            if (message.addedTime() != editableChatMessage.getAddedTime()) {
                continue;
            }

            return editableChatMessage;
        }

        return new EditableChatMessage(null, 0);
    }

    private static void openChat(String message) {
        ((MinecraftAccessor) Minecraft.getInstance()).openChat(message);
    }
}
