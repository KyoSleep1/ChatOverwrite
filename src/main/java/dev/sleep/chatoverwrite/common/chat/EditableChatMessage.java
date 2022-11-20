package dev.sleep.chatoverwrite.common.chat;

import lombok.Getter;
import net.minecraft.network.chat.PlayerChatMessage;

public class EditableChatMessage {

    @Getter
    private final PlayerChatMessage ChatMessage;

    @Getter
    private final int AddedTime;

    /** Used Server Side only **/
    public EditableChatMessage(PlayerChatMessage chatMessage) {
        this.ChatMessage = chatMessage;
        this.AddedTime = -444444444;
    }

    /** Used Client side only (addedTime correspond to a client local variable) **/
    public EditableChatMessage(PlayerChatMessage chatMessage, int addedTime) {
        this.ChatMessage = chatMessage;
        this.AddedTime = addedTime;
    }
}
