package dev.sleep.betterchat.client.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ClientChatHandler {

    private static final HashMap<String, UUID> MESSAGE_OWNER_LIST = new HashMap<>();

    public static void addToOwnerList(Minecraft minecraft, Component component, UUID profileUUID) {
        enhancedPut(minecraft, component, profileUUID);
    }

    private static void enhancedPut(Minecraft minecraft, Component component, UUID profileUUID) {
        ChatComponent chatComponent = minecraft.gui.getChat();
        int i = Mth.floor((double) chatComponent.getWidth() / chatComponent.getScale());
        i -= 9 + 4 + 2;

        List<FormattedCharSequence> list = ComponentRenderUtils.wrapComponents(component, i, minecraft.font);
        for (FormattedCharSequence formattedCharSequence : list) {
            MESSAGE_OWNER_LIST.put(getLegibleText(formattedCharSequence), profileUUID);
        }
    }

    public static void clearOwnerList() {
        MESSAGE_OWNER_LIST.clear();
    }

    public static boolean isMessageOwner(FormattedCharSequence charSequence) {
        AbstractClientPlayer clientPlayer = Minecraft.getInstance().player;
        UUID messageOwnerUUID = MESSAGE_OWNER_LIST.get(ClientChatHandler.getLegibleText(charSequence));

        if (messageOwnerUUID == null) {
            return false;
        }

        return messageOwnerUUID.equals(Objects.requireNonNull(clientPlayer).getUUID());
    }

    private static String getLegibleText(FormattedCharSequence charSequence) {
        StringBuilder stringBuilder = new StringBuilder();

        charSequence.accept((index, style, codePoints) -> {
            stringBuilder.appendCodePoint(codePoints);
            return true;
        });

        return stringBuilder.toString().strip();
    }
}
