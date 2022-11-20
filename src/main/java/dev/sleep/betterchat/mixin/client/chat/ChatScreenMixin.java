package dev.sleep.betterchat.mixin.client.chat;

import dev.sleep.betterchat.client.chat.ClientChatHandler;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Inject(method = "keyPressed(III)Z", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"))
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!ClientChatHandler.INSTANCE.isEditingMessage()) {
            return;
        }

        ClientChatHandler.INSTANCE.openChat("");
        ClientChatHandler.INSTANCE.messageCurrentlyEditing = null;
    }

}
