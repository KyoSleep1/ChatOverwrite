package dev.sleep.betterchat.client.chat.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.sleep.betterchat.Reference;
import dev.sleep.betterchat.client.chat.gui.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

// Height is lowercase because loombok does some strange sh*t with the getters. (getHEIGHT instead of getHeight) :(
public class ChatButton {

    private final static ResourceLocation CHAT_BUTTON_LOCATION = new ResourceLocation(Reference.MODID, "textures/guis/chat/message_icons.png");
    private final static int TEXTURE_SIZE = 32;

    private final int WIDTH, HEIGHT, MARGIN_X, MARGIN_Y, DEFAULT_U, DEFAULT_V, HOVER_U, HOVER_V, TEXTURE_WIDTH, TEXTURE_HEIGHT;
    private final String TOOL_TIP_TEXT;

    public ChatButton(String tooltipText, int width, int height, int marginX, int marginY, int defaultU, int defaultV, int hoverU, int hoverV, int textureWidth, int textureHeight) {
        this.TOOL_TIP_TEXT = tooltipText;

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
    }

    public void render(PoseStack poseStack, float guiScale, int positionX, int positionY) {
        if (this.isHovered(guiScale, positionX, positionY)) {
            GuiUtil.drawTexture(CHAT_BUTTON_LOCATION, poseStack, positionX, positionY, HOVER_U, HOVER_V, TEXTURE_WIDTH, TEXTURE_HEIGHT, TEXTURE_SIZE);
            renderTooltip(poseStack, guiScale);
            return;
        }

        GuiUtil.drawTexture(CHAT_BUTTON_LOCATION, poseStack, positionX, positionY, DEFAULT_U, DEFAULT_V, TEXTURE_WIDTH, TEXTURE_HEIGHT, TEXTURE_SIZE);
    }

    private void renderTooltip(PoseStack poseStack, float guiScale) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen == null) {
            return;
        }

        minecraft.screen.renderTooltip(poseStack, Component.literal(TOOL_TIP_TEXT), this.getScaledMouseX(guiScale), this.getScaledMouseY(guiScale));
    }

    public boolean isHovered(float guiScale, int positionX, int positionY) {
        return isUnderXBox(guiScale, positionX) && isUnderYBox(guiScale, positionY);
    }

    private boolean isUnderXBox(float guiScale, int positionX) {
        return this.getScaledMouseX(guiScale) >= (positionX - MARGIN_X) && this.getScaledMouseX(guiScale) < positionX + WIDTH;
    }

    private boolean isUnderYBox(float guiScale, int positionY) {
        return this.getScaledMouseY(guiScale) >= (positionY - 1) && this.getScaledMouseY(guiScale) < positionY + HEIGHT;
    }

    public int getScaledMouseX(float guiScale) {
        Minecraft minecraft = Minecraft.getInstance();

        int mouseX = getMouseX(minecraft);
        return (int) ((mouseX - 4.0) / guiScale);
    }

    public int getScaledMouseY(float guiScale) {
        Minecraft minecraft = Minecraft.getInstance();

        int mousePositionY = getMouseY(minecraft);
        double scaledMouseY = (double) (minecraft.getWindow().getGuiScaledHeight() - mousePositionY) - 40.0;

        return (int) -(scaledMouseY / (guiScale * minecraft.options.chatLineSpacing().get() + 1.0)) - 4;
    }

    private int getMouseX(Minecraft minecraft) {
        return (int) (minecraft.mouseHandler.xpos() * (double) minecraft.getWindow().getGuiScaledWidth() / (double) minecraft.getWindow().getScreenWidth());
    }

    private int getMouseY(Minecraft minecraft) {
        return (int) (minecraft.mouseHandler.ypos() * (double) minecraft.getWindow().getGuiScaledHeight() / (double) minecraft.getWindow().getScreenHeight());
    }
}
