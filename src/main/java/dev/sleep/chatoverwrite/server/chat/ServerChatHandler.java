package dev.sleep.chatoverwrite.server.chat;

import dev.sleep.chatoverwrite.common.chat.AbstractChatHandler;
import dev.sleep.chatoverwrite.common.chat.EditableChatMessage;

import java.time.Instant;
import java.util.UUID;

public class ServerChatHandler extends AbstractChatHandler {

    public static final ServerChatHandler INSTANCE = new ServerChatHandler();

    @Override
    public void removeFromAllLists(EditableChatMessage chatMessage) {
        UUID signerUUID = chatMessage.getChatMessage().signer().profileId();
        Instant timeStamp = chatMessage.getChatMessage().timeStamp();
        PLAYERS_MESSAGES_LIST.remove(signerUUID, timeStamp);
    }
}
