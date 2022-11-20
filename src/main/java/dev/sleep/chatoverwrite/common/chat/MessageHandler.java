package dev.sleep.chatoverwrite.common.chat;

import dev.sleep.chatoverwrite.mixin.client.chat.ChatComponentAccessor;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class MessageHandler {

    /**
     * Used to get a Formatted Message (Without Player's DisplayName) text from a GuiMessage
     */
    public static String getFormattedContentText(GuiMessage message) {
        String[] strippedMessage = MessageHandler.getLegibleText(Objects.requireNonNull(message).content().getVisualOrderText()).split(" ");
        return MessageHandler.getCleanText(strippedMessage, 0);
    }

    public static GuiMessage getMessageFromLine(GuiMessage.Line messageLine) {
        List<GuiMessage> allMessagesList = ((ChatComponentAccessor) Minecraft.getInstance().gui.getChat()).getAllMessagesList();

        for (GuiMessage fetchedMessage : allMessagesList) {
            if (fetchedMessage.addedTime() != messageLine.addedTime()) {
                continue;
            }

            return fetchedMessage;
        }

        return null;
    }

    /**
     * Used to get Legible text from codePoints
     **/
    public static String getLegibleText(FormattedCharSequence charSequence) {
        StringBuilder stringBuilder = new StringBuilder();

        charSequence.accept((index, style, codePoints) -> {
            stringBuilder.appendCodePoint(codePoints);
            return true;
        });

        return stringBuilder.toString();
    }

    /* Used to get the player message without the actual player's DisplayName
     * Extra steps are used to ensure compatibility with plugins/mods that change the chat structure
     * Example:
     *      Original: <Player468> Hi!
     *      Modified: > Player468 - Hi!
     */
    private static String getCleanText(String[] splittedText, int startIndex) {
        Component playerDisplayName = Objects.requireNonNull(Minecraft.getInstance().player).getDisplayName();
        String messageLine = splittedText[startIndex];

        if (!messageLine.contains(playerDisplayName.getString())) {
            startIndex++;
            return MessageHandler.getCleanText(splittedText, startIndex);
        }

        startIndex++;
        return buildMessage(splittedText, startIndex);
    }

    private static String buildMessage(String[] splittedText, int startIndex) {
        StringJoiner joiner = new StringJoiner(" ");

        for (int i = startIndex; i < splittedText.length; i++) {
            joiner.add(splittedText[i]);
        }

        return joiner.toString();
    }
}
