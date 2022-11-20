package dev.sleep.chatoverwrite.mixin.client.chat;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ChatComponent.class)
public interface ChatComponentAccessor {

    @Accessor(value = "allMessages")
    List<GuiMessage> getAllMessagesList();

    @Accessor(value = "trimmedMessages")
    List<GuiMessage.Line> getTrimmedMessagesList();

    @Invoker(value = "addMessage")
    void addMessage(Component chatComponent, @Nullable MessageSignature headerSignature, int addedTime, @Nullable GuiMessageTag tag, boolean onlyTrim);
}
