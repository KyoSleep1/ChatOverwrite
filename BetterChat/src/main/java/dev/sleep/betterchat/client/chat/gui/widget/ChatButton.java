package dev.sleep.betterchat.client.chat.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.sleep.betterchat.Reference;
import dev.sleep.betterchat.client.chat.gui.GuiUtil;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

// Height is lowercase because loombok does some strange sh*t with the getters. (getHEIGHT instead of getHeight) :(
public class ChatButton {

    private final static ResourceLocation CHAT_BUTTON_LOCATION = new ResourceLocation(Reference.MODID, "textures/guis/chat/message_icons.png");
    private final static int TEXTURE_SIZE = 32;

    @Getter
    private final int DEFAULT_U, DEFAULT_V, HOVER_U, HOVER_V, WIDTH, Height;

    public ChatButton(int defaultU, int defaultV, int hoverU, int hoverV, int width, int height){
        this.DEFAULT_U = defaultU;
        this.DEFAULT_V = defaultV;

        this.HOVER_U = hoverU;
        this.HOVER_V = hoverV;

        this.WIDTH = width;
        this.Height = height;
    }

    public void render(PoseStack poseStack, int positionX, int positionY){
        if(this.isHovered()){
            GuiUtil.drawTexture(CHAT_BUTTON_LOCATION, poseStack, positionX, positionY, HOVER_U, HOVER_V, WIDTH, Height, TEXTURE_SIZE);
            return;
        }

        GuiUtil.drawTexture(CHAT_BUTTON_LOCATION, poseStack, positionX, positionY, DEFAULT_U, DEFAULT_V, WIDTH, Height, TEXTURE_SIZE);
    }

    public boolean isHovered(){
        return false;
    }
}
