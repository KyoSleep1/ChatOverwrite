package dev.sleep.betterchat.mixin.client.chat;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ChatComponent.class)
public interface ChatComponentAccessor {

    @Accessor(value = "trimmedMessages")
    List<GuiMessage.Line> getTrimmedMessagesList();

    @Accessor(value = "allMessages")
    List<GuiMessage> getAllMessagesList();
}
