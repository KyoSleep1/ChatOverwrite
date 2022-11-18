package dev.sleep.betterchat.common.chat;

import lombok.Getter;
import net.minecraft.network.chat.PlayerChatMessage;

public class EditableChatMessage {

    @Getter
    private final PlayerChatMessage ChatMessage;

    @Getter
    private final int AddedTime;

    public EditableChatMessage(PlayerChatMessage chatMessage, int addedTime) {
        this.ChatMessage = chatMessage;
        this.AddedTime = addedTime;
    }
}
