package dev.sleep.betterchat.client.gui.chat.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.sleep.betterchat.Reference;
import dev.sleep.betterchat.client.MouseHelper;
import dev.sleep.betterchat.client.gui.GuiUtil;
import lombok.Getter;
import net.minecraft.client.GuiMessage;
import net.minecraft.resources.ResourceLocation;

public class ChatButton {

    private final static ResourceLocation CHAT_BUTTON_LOCATION = new ResourceLocation(Reference.MODID, "textures/guis/chat/message_icons.png");
    private final static int TEXTURE_SIZE = 32;

    @Getter
    private final ChatButton.OnPress OnPress;

    private final int WIDTH, HEIGHT, MARGIN_X, MARGIN_Y, DEFAULT_U, DEFAULT_V, HOVER_U, HOVER_V, TEXTURE_WIDTH, TEXTURE_HEIGHT;


    public ChatButton(int width, int height, int marginX, int marginY, int defaultU, int defaultV, int hoverU, int hoverV, int textureWidth, int textureHeight, ChatButton.OnPress onPress) {
        this.WIDTH = width;
        this.HEIGHT = height;

        this.MARGIN_X = marginX;
        this.MARGIN_Y = marginY;

        this.DEFAULT_U = defaultU;
        this.DEFAULT_V = defaultV;

        this.HOVER_U = hoverU;
        this.HOVER_V = hoverV;

        this.TEXTURE_WIDTH = textureWidth;
        this.TEXTURE_HEIGHT = textureHeight;

        this.OnPress = onPress;
    }

    public void render(int visibleMessageIndex, PoseStack poseStack, float guiScale, int positionX, int positionY) {
        if (!this.isHovered(visibleMessageIndex, guiScale, positionX, positionY)) {
            GuiUtil.drawTexture(CHAT_BUTTON_LOCATION, poseStack, positionX, positionY, DEFAULT_U, DEFAULT_V, TEXTURE_WIDTH, TEXTURE_HEIGHT, TEXTURE_SIZE);
            return;
        }

        GuiUtil.drawTexture(CHAT_BUTTON_LOCATION, poseStack, positionX, positionY, HOVER_U, HOVER_V, TEXTURE_WIDTH, TEXTURE_HEIGHT, TEXTURE_SIZE);
    }

    public boolean isHovered(int visibleMessageIndex, float guiScale, int positionX, int positionY) {
        return isUnderXBox(guiScale, positionX) && isUnderYBox(visibleMessageIndex, guiScale, positionY);
    }

    private boolean isUnderXBox(float guiScale, int positionX) {
        return MouseHelper.getScaledMouseX(guiScale) >= (positionX - MARGIN_X) && MouseHelper.getScaledMouseX(guiScale) < positionX + WIDTH;
    }

    private boolean isUnderYBox(int visibleMessageIndex, float guiScale, int positionY) {
        positionY = positionY + (MARGIN_Y * visibleMessageIndex);
        return MouseHelper.getScaledMouseY(guiScale) >= positionY && MouseHelper.getScaledMouseY(guiScale) < positionY + HEIGHT;
    }

    public interface OnPress {
        void press(GuiMessage message);
    }
}
