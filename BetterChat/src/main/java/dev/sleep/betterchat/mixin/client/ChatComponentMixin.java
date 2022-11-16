package dev.sleep.betterchat.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.sleep.betterchat.client.chat.ClientChatHandler;
import dev.sleep.betterchat.client.chat.gui.widget.ChatButton;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private int chatScrollbarPos;

    @Shadow
    @Final
    private List<GuiMessage.Line> trimmedMessages;

    @Shadow
    protected abstract boolean isChatHidden();

    @Shadow
    public abstract int getLinesPerPage();

    @Shadow
    protected abstract boolean isChatFocused();

    @Shadow
    public abstract double getScale();

    @Shadow
    protected abstract int getLineHeight();

    private final ChatButton EDIT_BUTTON = new ChatButton(2, 18, 18, 18, 12, 12),
            DELETE_BUTTON = new ChatButton(2, 2, 19, 2, 11, 12);

    /**
     * @author KyoSleep
     * @reason Draw Edit/Delete button during message rendering, remove Scroll Indicator (It's ugly) and Message Safety Icon indicator
     * (It's useless and scares people)
     */
    @Overwrite
    public void render(PoseStack poseStack, int tickCount) {
        ChatComponent chatComponent = (ChatComponent) (Object) this;
        if (this.isChatHidden() || this.trimmedMessages.size() <= 0) {
            return;
        }

        float guiScale = (float) this.getScale();
        int chatWidthBasedOnScale = Mth.ceil(chatComponent.getWidth() / guiScale);

        double chatOpacityFactor = this.minecraft.options.chatOpacity().get() * (double) 0.9f + (double) 0.1f;
        double textBackgroundOpacity = this.minecraft.options.textBackgroundOpacity().get();
        double chatSpacing = this.minecraft.options.chatLineSpacing().get();

        poseStack.pushPose();
        poseStack.scale(guiScale, guiScale, 1.0F);
        poseStack.translate(2.0, 8.0, 0.0);

        renderMessages(poseStack, guiScale, this.trimmedMessages.size(), this.getLinesPerPage(), this.getLineHeight(), chatWidthBasedOnScale, chatOpacityFactor,
                textBackgroundOpacity, chatSpacing, this.isChatFocused(), tickCount);
        poseStack.popPose();
    }

    private void renderMessages(PoseStack poseStack, float guiScale, int visibleMessagesSize, int linesPerPage, int lineHeight, int chatWidthBasedOnScale,
                                double chatOpacityFactor, double textBackgroundOpacity, double chatSpacing, boolean chatFocused, int tickCount) {
        for (int visibleMessageIndex = 0; (visibleMessageIndex + chatScrollbarPos) < visibleMessagesSize &&
                visibleMessageIndex < linesPerPage; visibleMessageIndex++) {
            GuiMessage.Line visibleMessage = this.trimmedMessages.get(visibleMessageIndex + this.chatScrollbarPos);
            if (visibleMessage == null) {
                continue;
            }

            int timeSinceMessageAdded = tickCount - visibleMessage.addedTime();
            if (((timeSinceMessageAdded >= 200) && !chatFocused)) {
                continue;
            }

            double alphaColor = chatFocused ? 1.0 : ChatComponentMixin.getTimeFactor(timeSinceMessageAdded);
            int chatOpacityColor = (int) ((255.0 * alphaColor) * chatOpacityFactor);
            int backgroundOpacityColor = (int) ((255.0 * alphaColor) * textBackgroundOpacity);

            if (chatOpacityColor <= 3) {
                continue;
            }

            double chatMargin = -8.0 * (chatSpacing + 1.0) + 4.0 * chatSpacing;
            int lineMaxHeight = -visibleMessageIndex * lineHeight;

            float textPositionX = 0.0F;
            int textPositionY = (int) ((double) lineMaxHeight + chatMargin);

            RenderSystem.enableBlend();
            poseStack.pushPose();
            poseStack.translate(0.0, 0.0, 50.0);

            ChatComponent.fill(poseStack, -4, (lineMaxHeight - lineHeight), chatWidthBasedOnScale + 16, lineMaxHeight, backgroundOpacityColor << 24);
            if (shouldRenderButtons(visibleMessage)) {
                textPositionX = 20.0F;
                renderButtons(poseStack, guiScale, visibleMessageIndex, textPositionY);
            }

            this.minecraft.font.drawShadow(poseStack, visibleMessage.content(), textPositionX, (float) textPositionY, 0xFFFFFF + (chatOpacityColor << 24));
            poseStack.popPose();
            RenderSystem.disableBlend();
        }
    }

    private void renderButtons(PoseStack poseStack, float guiScale, int visibleMessageIndex, int textPositionY) {
        poseStack.pushPose();
        poseStack.scale(0.6100F, 0.6100F, 1.0F);

        DELETE_BUTTON.render(poseStack, guiScale, 1, (calculateIconSpacing(visibleMessageIndex) + textPositionY) + 10);
        EDIT_BUTTON.render(poseStack, guiScale, 16, (calculateIconSpacing(visibleMessageIndex) + textPositionY) + 10);

        poseStack.popPose();
    }

    @Inject(method = "clearMessages(Z)V", at = @At("HEAD"))
    public void clearOwnerList(boolean clearSentMsgHistory, CallbackInfo ci){
        ClientChatHandler.clearOwnerList();
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/StringSplitter;componentStyleAtWidth(Lnet/minecraft/util/FormattedCharSequence;I)Lnet/minecraft/network/chat/Style;"),
            method = "getClickedComponentStyleAt(DD)Lnet/minecraft/network/chat/Style;",
            index = 1
    )
    public int correctClickPosition(int x) {
        return x - 18;
    }

    private int calculateIconSpacing(int messageIndex){
        return -15 - (messageIndex * 6);
    }

    private boolean shouldRenderButtons(GuiMessage.Line line) {
        return ClientChatHandler.isMessageOwner(line.content());
    }

    private static double getTimeFactor(int i) {
        double d = (double) i / 200.0;
        d = 1.0 - d;
        d *= 10.0;
        d = Mth.clamp(d, 0.0, 1.0);
        d *= d;
        return d;
    }
}
