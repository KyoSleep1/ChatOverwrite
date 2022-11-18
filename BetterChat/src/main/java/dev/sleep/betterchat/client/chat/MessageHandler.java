package dev.sleep.betterchat.client.chat;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.Objects;
import java.util.StringJoiner;

public class MessageHandler {

    public static String getMessageTextAndFormat(GuiMessage message) {
        String[] strippedMessage = MessageHandler.getLegibleText(Objects.requireNonNull(message).content().getVisualOrderText()).split(" ");
        return MessageHandler.getCleanText(strippedMessage, 0);
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
