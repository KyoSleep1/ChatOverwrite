package dev.sleep.betterchat.mixin.client;

import dev.sleep.betterchat.client.chat.ClientChatHandler;
import dev.sleep.betterchat.common.chat.EditableChatMessage;
import dev.sleep.betterchat.common.network.NetworkManager;
import dev.sleep.betterchat.common.network.packet.PacketNotifyMessageEdited;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {

    @Shadow
    protected abstract ChatMessageContent buildSignedContent(String plain, @Nullable Component decorated);

    @Shadow
    protected abstract MessageSigner createMessageSigner();

    @Shadow
    @Final
    public ClientPacketListener connection;

    @Shadow
    protected abstract MessageSignature signMessage(MessageSigner signer, ChatMessageContent messageContent, LastSeenMessages lastSeenMessages);

    /**
     * @author KyoSleep
     * @reason Used to modify the normal Message sending in order to support message editing
     */
    @Overwrite
    private void sendChat(String plain, @Nullable Component decorated) {
        ChatMessageContent chatMessageContent = this.buildSignedContent(plain, decorated);
        MessageSigner messageSigner = this.createMessageSigner();

        if (!ClientChatHandler.INSTANCE.isEditingMessage()) {
            LastSeenMessages.Update update = this.connection.generateMessageAcknowledgements();
            MessageSignature messageSignature = this.signMessage(messageSigner, chatMessageContent, update.lastSeen());
            this.connection.send(new ServerboundChatPacket(chatMessageContent.plain(), messageSigner.timeStamp(), messageSigner.salt(), messageSignature, chatMessageContent.isDecorated(), update));
            return;
        }

        EditableChatMessage editedChatMessage = ClientChatHandler.INSTANCE.getMessageCurrentlyEditing();
        ClientPlayNetworking.send(NetworkManager.EDITED_MESSAGE_PACKET_ID, PacketNotifyMessageEdited.createWritedBuf(editedChatMessage, chatMessageContent));
    }
}
