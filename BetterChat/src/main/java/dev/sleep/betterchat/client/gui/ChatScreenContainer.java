package dev.sleep.betterchat.client.gui;

import dev.sleep.betterchat.client.gui.widget.ChatButton;
import dev.sleep.betterchat.mixin.client.MinecraftAccessor;
import lombok.Getter;
import net.minecraft.client.Minecraft;

//This class works as a storage for the buttons
public class ChatScreenContainer {

    @Getter
    private static final ChatButton EditButton = new ChatButton("TEST 1", 1, 8, 7, 6, 2, 18,
            18, 18, 12, 12, chatButton -> {
        ((MinecraftAccessor) Minecraft.getInstance()).openChat("TEST");
    });

    @Getter
    private static final ChatButton DeleteButton = new ChatButton("TEST 2", 5, 8, 3, 6, 2, 2,
            19, 2, 11, 12, chatButton -> {
        ((MinecraftAccessor) Minecraft.getInstance()).openChat("TEST 2");
    });
}
