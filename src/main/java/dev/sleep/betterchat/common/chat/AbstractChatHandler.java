package dev.sleep.betterchat.common.chat;

import com.google.common.collect.HashBasedTable;
import net.minecraft.world.entity.player.Player;

import java.time.Instant;
import java.util.UUID;

public abstract class AbstractChatHandler {

    protected final HashBasedTable<UUID, Instant, EditableChatMessage> PLAYERS_MESSAGES_LIST = HashBasedTable.create();

    public void addToEditableMessageList(EditableChatMessage editableChatMessage) {
        UUID signerUUID = editableChatMessage.getChatMessage().signer().profileId();
        Instant timeStamp = editableChatMessage.getChatMessage().timeStamp();
        PLAYERS_MESSAGES_LIST.put(signerUUID, timeStamp, editableChatMessage);
    }

    public void removeMessageFromUUID(UUID uuidToDelete){
        PLAYERS_MESSAGES_LIST.row(uuidToDelete).clear();
    }

    public void deleteMessage(EditableChatMessage chatMessage) {
        this.removeFromAllLists(chatMessage);
    }

    public abstract void removeFromAllLists(EditableChatMessage chatMessage);

    public void clearPlayerEditableMessagesList() {
        PLAYERS_MESSAGES_LIST.clear();
    }

    public boolean isMessageOwner(EditableChatMessage chatMessage, Player sender) {
        if (chatMessage == null) {
            return false;
        }

        UUID playerUUID = chatMessage.getChatMessage().signer().profileId();
        return sender.getUUID().equals(playerUUID);
    }

    public EditableChatMessage getEMessageByTimeStamp(Instant timeStamp) {
        for (EditableChatMessage chatMessage : PLAYERS_MESSAGES_LIST.values()) {
            long messageEpochSecond = chatMessage.getChatMessage().timeStamp().getEpochSecond();
            long timeStampEpochSecond = timeStamp.getEpochSecond();

            if(messageEpochSecond != timeStampEpochSecond){
                continue;
            }

            return chatMessage;
        }

        return null;
    }
}
