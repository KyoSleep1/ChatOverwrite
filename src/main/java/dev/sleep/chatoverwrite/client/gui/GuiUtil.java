package dev.sleep.chatoverwrite.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;

public class GuiUtil {

    public static void drawCenteredFontWithBlackOutline(PoseStack poseStack, Font font, String text, int positionX, int positionY, int colorHex) {
        Gui.drawCenteredString(poseStack, font, text, positionX + 1, positionY, 0);
        Gui.drawCenteredString(poseStack, font, text, positionX - 1, positionY, 0);

        Gui.drawCenteredString(poseStack, font, text, positionX, positionY + 1, 0);
        Gui.drawCenteredString(poseStack, font, text, positionX, positionY - 1, 0);

        Gui.drawCenteredString(poseStack, font, text, positionX, positionY, colorHex);
    }

    public static void drawCenteredFont(PoseStack poseStack, Font font, String text, int positionX, int positionY, int colorHex) {
        Gui.drawCenteredString(poseStack, font, text, positionX, positionY, colorHex);
    }

    public static void drawFont(PoseStack matrixStackIn, Font font, String text, int positionX, int positionY, int colorHex) {
        font.draw(matrixStackIn, text, positionX, positionY, colorHex);
    }

    public static void drawTexture(ResourceLocation resourceLocation, PoseStack poseStack, int positionX, int positionY, int u, int v, int width, int height, int textureSize) {
        GuiUtil.bindTexture(resourceLocation);
        GuiComponent.blit(poseStack, positionX, positionY,  u, v, width, height, textureSize, textureSize);
    }

    public static void bindTexture(ResourceLocation resourceLocation) {
        RenderSystem.setShaderTexture(0, resourceLocation);
    }

    public static int getCenterX(int width) {
        return (getWidthRelativeToScreen()) - width / 2;
    }

    public static int getCenterY(int height) {
        return (getHeightRelativeToScreen()) - height / 2;
    }

    public static int getWidthRelativeToScreen() {
        return Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2;
    }

    public static int getHeightRelativeToScreen() {
        return Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2;
    }

    public static int getLeftPos(int screenWidth, int imageWidth) {
        return (screenWidth - imageWidth) / 2;
    }

    public static int getRightPos(int screenHeight, int imageHeight) {
        return (screenHeight - imageHeight) / 2;
    }
}
