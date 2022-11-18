package dev.sleep.betterchat.client;

import net.minecraft.client.Minecraft;

public class MouseHelper {

    public static int getMouseX() {
        Minecraft minecraft = Minecraft.getInstance();
        return (int) (minecraft.mouseHandler.xpos() * (double) minecraft.getWindow().getGuiScaledWidth() / (double) minecraft.getWindow().getScreenWidth());
    }

    public static int getMouseY() {
        Minecraft minecraft = Minecraft.getInstance();
        return (int) (minecraft.mouseHandler.ypos() * (double) minecraft.getWindow().getGuiScaledHeight() / (double) minecraft.getWindow().getScreenHeight());
    }

    public static int getScaledMouseX(double guiScale) {
        return (int) ((MouseHelper.getMouseX() - 4.0) / guiScale);
    }

    public static int getScaledMouseY(double guiScale) {
        Minecraft minecraft = Minecraft.getInstance();
        double scaledMouseY = (double) (minecraft.getWindow().getGuiScaledHeight() - MouseHelper.getMouseY()) - 40.0;
        return (int) -(scaledMouseY / (guiScale * minecraft.options.chatLineSpacing().get() + 1.0)) - 4;
    }
}
