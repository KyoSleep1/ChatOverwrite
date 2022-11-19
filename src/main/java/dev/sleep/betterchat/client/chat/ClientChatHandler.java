package dev.sleep.betterchat.client.chat;

import dev.sleep.betterchat.common.chat.AbstractChatHandler;
import dev.sleep.betterchat.common.chat.EditableChatMessage;
import dev.sleep.betterchat.common.chat.MessageHandler;
import dev.sleep.betterchat.common.network.NetworkManager;
import dev.sleep.betterchat.common.network.packet.PacketNotifyMessageDelete;
import dev.sleep.betterchat.mixin.client.MinecraftAccessor;
import dev.sleep.betterchat.mixin.client.chat.ChatComponentAccessor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientChatHandler extends AbstractChatHandler {

    public static final ClientChatHandler INSTANCE = new ClientChatHandler();

    /**
     * Overload for the default implementation AbstractChatHandler#isMessageOwner
     **/
    public boolean isMessageOwner(int addedTime) {
        EditableChatMessage chatMessage = this.getEditableMessageByAddedTime(addedTime);
        return this.isMessageOwner(chatMessage, Minecraft.getInstance().player);
    }

    public void editMessage(GuiMessage message) {
        String textFromEditedButton = MessageHandler.getFormattedContentText(message);
        ClientChatHandler.openChat(textFromEditedButton);
    }

    public void editMessage(GuiMessage message, boolean notifyOthers) {
        EditableChatMessage chatMessage = this.getEditableMessageByAddedTime(message.addedTime());
        String textFromEditedButton = MessageHandler.getFormattedContentText(message);
        ClientChatHandler.openChat(textFromEditedButton);
    }

    public void deleteMessage(GuiMessage message) {
        ClientChatHandler.openChat("");
        EditableChatMessage chatMessage = this.getEditableMessageByAddedTime(message.addedTime());

        if (chatMessage == null) {
            return;
        }

        ClientPlayNetworking.send(NetworkManager.DELETED_MESSAGE_PACKET_ID, PacketNotifyMessageDelete.createWritedBuf(chatMessage));
    }

    @Override
    public void removeFromAllLists(EditableChatMessage chatMessage) {
        ChatComponent chatComponent = Minecraft.getInstance().gui.getChat();
        this.removeFromPlayersMessagesList(chatMessage);
        this.removeFromAllMessagesList(chatMessage, chatComponent);
        this.removeFromTrimmedMessagesList(chatMessage, chatComponent);
    }

    private void removeFromPlayersMessagesList(EditableChatMessage chatMessage) {
        UUID senderUUID = chatMessage.getChatMessage().signer().profileId();
        Instant timeStamp = chatMessage.getChatMessage().timeStamp();

        this.PLAYERS_MESSAGES_LIST.remove(senderUUID, timeStamp);
    }

    private void removeFromAllMessagesList(EditableChatMessage chatMessage, ChatComponent chatComponent) {
        GuiMessage message = this.getGMessageByEMessage(chatMessage);
        ((ChatComponentAccessor) chatComponent).getAllMessagesList().remove(message);
    }

    private void removeFromTrimmedMessagesList(EditableChatMessage chatMessage, ChatComponent chatComponent) {
        List<GuiMessage.Line> trimmedMessagesList = ((ChatComponentAccessor) chatComponent).getTrimmedMessagesList();
        List<GuiMessage.Line> trimmedMessagesToRemove = new ArrayList<>();

        for (GuiMessage.Line trimmedMessage : trimmedMessagesList) {
            if (trimmedMessage.addedTime() != chatMessage.getAddedTime()) {
                continue;
            }

            trimmedMessagesToRemove.add(trimmedMessage);
        }

        for (GuiMessage.Line trimmedMesageToRemove : trimmedMessagesToRemove) {
            trimmedMessagesList.remove(trimmedMesageToRemove);
        }
    }

    private EditableChatMessage getEditableMessageByAddedTime(int addedTime) {
        for (EditableChatMessage editableChatMessage : PLAYERS_MESSAGES_LIST.values()) {
            if (editableChatMessage.getAddedTime() != addedTime) {
                continue;
            }

            return editableChatMessage;
        }

        return null;
    }

    /**
     * The actual method name is getGuiMessageByEditableMessage.
     * I don't like large stuff... ( ͡° ͜ʖ ͡°)
     * no.
     * <p>
     * Be careful when using this method because it doesn't return null, instead it returns an empty object
     */
    public GuiMessage getGMessageByEMessage(EditableChatMessage chatMessage) {
        ChatComponent chatComponent = Minecraft.getInstance().gui.getChat();
        List<GuiMessage> allMessagesList = ((ChatComponentAccessor) chatComponent).getAllMessagesList();

        for (GuiMessage guiMessage : allMessagesList) {
            if (guiMessage.addedTime() != chatMessage.getAddedTime()) {
                continue;
            }

            return guiMessage;
        }

        return null;
    }

    //openChat
    private static void openChat(String message) {
        //opens the chat
        ((MinecraftAccessor) Minecraft.getInstance()).openChat(message); //chat open
        //end of open chat
    }
    //it wasn't the end
    //the actual end
}
