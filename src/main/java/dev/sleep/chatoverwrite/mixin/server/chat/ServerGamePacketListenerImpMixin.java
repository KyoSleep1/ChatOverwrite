package dev.sleep.chatoverwrite.mixin.server.chat;

import dev.sleep.chatoverwrite.common.chat.EditableChatMessage;
import dev.sleep.chatoverwrite.server.chat.ServerChatHandler;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImpMixin {

    @Inject(method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;)V", at = @At("HEAD"))
    private void addEditableMessageToList(PlayerChatMessage message, CallbackInfo ci){
        if(message.signer().isSystem()){
            return;
        }

        ServerChatHandler.INSTANCE.addToEditableMessageList(new EditableChatMessage(message));
    }
}
