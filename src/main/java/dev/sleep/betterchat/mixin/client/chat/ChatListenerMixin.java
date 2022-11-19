package dev.sleep.betterchat.mixin.client.chat;

import dev.sleep.betterchat.client.chat.ClientChatHandler;
import dev.sleep.betterchat.common.chat.EditableChatMessage;
import net.minecraft.Util;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.client.multiplayer.chat.ChatTrustLevel;
import net.minecraft.network.chat.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.time.Instant;

@Mixin(ChatListener.class)
public abstract class ChatListenerMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    protected abstract void onChatChainBroken();

    @Shadow
    protected abstract ChatTrustLevel evaluateTrustLevel(PlayerChatMessage chatMessage, Component decoratedServerContent, @Nullable PlayerInfo playerInfo, Instant timestamp);

    @Shadow
    protected abstract void narrateChatMessage(ChatType.Bound boundChatType, Component message);

    @Shadow
    protected abstract void logPlayerMessage(PlayerChatMessage message, ChatType.Bound boundChatType, @Nullable PlayerInfo playerInfo, ChatTrustLevel trustLevel);

    @Shadow
    private long previousMessageTime;

    /**
     * @author KyoSleep
     * @reason We need a way to create our EditableChatMessage, this method is perfect because it has all the params needed.
     */
    @Overwrite
    private boolean showMessageToPlayer(ChatType.Bound boundChatType, PlayerChatMessage chatMessage, Component decoratedServerContent, @Nullable PlayerInfo playerInfo, boolean onlyShowSecureChat, Instant timestamp) {
        ChatTrustLevel chatTrustLevel = this.evaluateTrustLevel(chatMessage, decoratedServerContent, playerInfo, timestamp);
        int messageAddedTime = minecraft.gui.getGuiTicks();

        if (chatTrustLevel == ChatTrustLevel.BROKEN_CHAIN) {
            this.onChatChainBroken();
            return true;
        }

        if (onlyShowSecureChat && chatTrustLevel.isNotSecure()) {
            return false;
        }

        if (this.minecraft.isBlocked(chatMessage.signer().profileId()) || chatMessage.isFullyFiltered()) {
            return false;
        }

        GuiMessageTag guiMessageTag = chatTrustLevel.createTag(chatMessage);
        MessageSignature messageSignature = chatMessage.headerSignature();
        FilterMask filterMask = chatMessage.filterMask();

        if (filterMask.isEmpty()) {
            addMessageToGui(chatMessage, boundChatType, decoratedServerContent, messageSignature, guiMessageTag, messageAddedTime);
        } else {
            Component component = filterMask.apply(chatMessage.signedContent());
            if (component != null) {
                addMessageToGui(chatMessage, boundChatType, boundChatType.decorate(component), messageSignature, guiMessageTag, messageAddedTime);
            }
        }

        this.logPlayerMessage(chatMessage, boundChatType, playerInfo, chatTrustLevel);
        this.previousMessageTime = Util.getMillis();
        return true;
    }


    private void addMessageToGui(PlayerChatMessage chatMessage, ChatType.Bound boundChatType,  Component component, MessageSignature messageSignature, GuiMessageTag guiMessageTag, int messageAddedTime) {
        this.addToEditableMessageListIfPossible(chatMessage, messageAddedTime);
        this.minecraft.gui.getChat().addMessage(component, messageSignature, guiMessageTag);
        this.narrateChatMessage(boundChatType, component);
    }

    private void addToEditableMessageListIfPossible(PlayerChatMessage chatMessage, int addedTime) {
        if (chatMessage.signer().isSystem()) {
            return;
        }

        ClientChatHandler.INSTANCE.addToEditableMessageList(new EditableChatMessage(chatMessage, addedTime));
    }
}
