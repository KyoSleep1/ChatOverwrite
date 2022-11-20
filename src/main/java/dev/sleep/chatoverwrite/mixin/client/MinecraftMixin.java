package dev.sleep.chatoverwrite.mixin.client;

import dev.sleep.chatoverwrite.client.chat.ClientChatHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "setLevel(Lnet/minecraft/client/multiplayer/ClientLevel;)V", at = @At("HEAD"))
    public void setLevel(ClientLevel clientLevel, CallbackInfo ci) {
        ClientChatHandler.INSTANCE.clearPlayerEditableMessagesList();
    }
}
