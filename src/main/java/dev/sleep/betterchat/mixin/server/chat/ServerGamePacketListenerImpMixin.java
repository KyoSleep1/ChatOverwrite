package dev.sleep.betterchat.mixin.server.chat;

import dev.sleep.betterchat.common.chat.EditableChatMessage;
import dev.sleep.betterchat.server.chat.ServerChatHandler;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImpMixin {

    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    public ServerPlayer player;

    @Shadow
    protected abstract void detectRateSpam();

    @Inject(method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;)V", at = @At("HEAD"))
    private void addEditableMessageToList(PlayerChatMessage message, CallbackInfo ci){
        if(message.signer().isSystem()){
            return;
        }

        ServerChatHandler.INSTANCE.addToEditableMessageList(new EditableChatMessage(message));
    }
}
