package dev.sleep.chatoverwrite.mixin.client.chat;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.sleep.chatoverwrite.client.MouseHelper;
import dev.sleep.chatoverwrite.client.chat.ClientChatHandler;
import dev.sleep.chatoverwrite.client.gui.chat.widget.ChatButton;
import dev.sleep.chatoverwrite.common.chat.MessageHandler;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private List<GuiMessage.Line> trimmedMessages;

    @Shadow
    @Final
    private List<GuiMessage> allMessages;

    @Shadow
    private int chatScrollbarPos;

    @Shadow
    private boolean newMessageSinceScroll;

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

    @Shadow
    protected abstract void logChatMessage(Component chatComponent, @Nullable GuiMessageTag tag);

    @Shadow
    public abstract void scrollChat(int posInc);

    @Shadow
    public abstract int getWidth();

    @Shadow
    private static double getTimeFactor(int counter) {
        return 0;
    }

    @Shadow
    protected abstract int getMessageIndexAt(double chatY);

    /**
     * Don't ask me why, but, buttons crash if they are not static
     **/
    private static final ChatButton EDIT_BUTTON = new ChatButton(1, 8, 7, 6, 2, 18,
            18, 18, 12, 12, ClientChatHandler.INSTANCE::editMessageAndNotify);

    private static final ChatButton DELETE_BUTTON = new ChatButton(5, 8, 3, 6, 2, 2,
            19, 2, 11, 12, ClientChatHandler.INSTANCE::deleteMessageAndNotify);

    /**
     * @author KyoSleep
     * @reason We need a way to know if the current FormattedCharSequence is the first entry so we hack "endOfEntry" to "startOfEntry"
     * this helps us to render icon in the correct line.
     */
    @Overwrite
    public void addMessage(Component chatComponent, @Nullable MessageSignature headerSignature, int addedTime, @Nullable GuiMessageTag tag, boolean onlyTrim) {
        this.logChatMessage(chatComponent, tag);

        int i = Mth.floor((double) this.getWidth() / this.getScale());
        if (tag != null && tag.icon() != null) {
            i -= tag.icon().width + 4 + 2;
        }

        List<FormattedCharSequence> list = ComponentRenderUtils.wrapComponents(chatComponent, i, this.minecraft.font);
        boolean bl = this.isChatFocused();

        for (int j = 0; j < list.size(); ++j) {
            FormattedCharSequence formattedCharSequence = list.get(j);
            if (bl && this.chatScrollbarPos > 0) {
                this.newMessageSinceScroll = true;
                this.scrollChat(1);
            }

            //Before: boolean bl2 = j == list.size() - 1;
            boolean bl2 = j == 0;
            this.trimmedMessages.add(0, new GuiMessage.Line(addedTime, formattedCharSequence, tag, bl2));
        }

        while (this.trimmedMessages.size() > 100) {
            this.trimmedMessages.remove(this.trimmedMessages.size() - 1);
        }

        if (!onlyTrim) {
            this.allMessages.add(0, new GuiMessage(addedTime, chatComponent, headerSignature, tag));
            while (this.allMessages.size() > 100) {
                this.allMessages.remove(this.allMessages.size() - 1);
            }
        }
    }

    @Inject(method = "clearMessages(Z)V", at = @At("HEAD"))
    public void clearMessageList(boolean clearSentMsgHistory, CallbackInfo ci) {
        ClientChatHandler.INSTANCE.clearPlayerEditableMessagesList();
    }

    /**
     * @author KyoSleep
     * @reason Draw Edit/Delete button during message rendering, remove Scroll Indicator (It's ugly) and Message Safety Icon indicator
     * (It's useless and scares people)
     */
    @Overwrite
    public void render(PoseStack poseStack, int tickCount) {
        if (this.isChatHidden() || this.trimmedMessages.size() <= 0) {
            return;
        }

        float guiScale = (float) this.getScale();
        int chatWidthBasedOnScale = Mth.ceil(this.getWidth() / guiScale);

        double chatOpacityFactor = this.minecraft.options.chatOpacity().get() * (double) 0.9f + (double) 0.1f;
        double textBackgroundOpacity = this.minecraft.options.textBackgroundOpacity().get();
        double chatSpacing = this.minecraft.options.chatLineSpacing().get();

        poseStack.pushPose();
        poseStack.scale(guiScale, guiScale, 1.0F);

        poseStack.translate(2.0, 8.0, 0.0);
        renderMessages(poseStack, chatWidthBasedOnScale, chatOpacityFactor, textBackgroundOpacity, chatSpacing, tickCount);

        poseStack.popPose();
    }

    private void renderMessages(PoseStack poseStack, int chatWidthBasedOnScale, double chatOpacityFactor, double textBackgroundOpacity, double chatSpacing, int tickCount) {
        for (int lineMessageIndex = 0; (lineMessageIndex + chatScrollbarPos) < this.trimmedMessages.size() && lineMessageIndex < this.getLinesPerPage(); lineMessageIndex++) {
            GuiMessage.Line visibleMessage = this.trimmedMessages.get(lineMessageIndex + this.chatScrollbarPos);
            if (visibleMessage == null) {
                continue;
            }

            int timeSinceMessageAdded = tickCount - visibleMessage.addedTime();
            if (((timeSinceMessageAdded >= 200) && !this.isChatFocused())) {
                continue;
            }

            double alphaColor = this.isChatFocused() ? 1.0 : ChatComponentMixin.getTimeFactor(timeSinceMessageAdded);
            int chatOpacityColor = (int) ((255.0 * alphaColor) * chatOpacityFactor);
            int backgroundOpacityColor = (int) ((255.0 * alphaColor) * textBackgroundOpacity);

            if (chatOpacityColor <= 3) {
                continue;
            }

            double chatMargin = -8.0 * (chatSpacing + 1.0) + 4.0 * chatSpacing;
            int lineMaxHeight = -lineMessageIndex * this.getLineHeight();

            int textPositionY = (int) ((double) lineMaxHeight + chatMargin);
            float textPositionX = 0.0F;

            RenderSystem.enableBlend();
            poseStack.pushPose();
            poseStack.translate(0.0, 0.0, 50.0);

            ChatComponent.fill(poseStack, -4, (lineMaxHeight - this.getLineHeight()), chatWidthBasedOnScale + 16, lineMaxHeight, backgroundOpacityColor << 24);
            if (shouldRenderButtons(visibleMessage, lineMessageIndex)) {
                textPositionX = 20.0F;
                renderButtons(poseStack, (float) this.getScale(), lineMessageIndex);
            }

            this.minecraft.font.drawShadow(poseStack, visibleMessage.content(), textPositionX, (float) textPositionY, 0xFFFFFF + (chatOpacityColor << 24));
            poseStack.popPose();
            RenderSystem.disableBlend();
        }
    }

    private boolean shouldRenderButtons(GuiMessage.Line line, int lineMessageIndex) {
        return line.endOfEntry() && isHoveringMessage(lineMessageIndex) && ClientChatHandler.INSTANCE.isMessageOwner(line.addedTime());
    }

    private boolean isHoveringMessage(int lineMessageIndex) {
        int modifiedScaledMouse = -MouseHelper.getScaledMouseY(this.getScale()) - 4;
        int messageIndex = this.getMessageIndexAt(modifiedScaledMouse);
        return messageIndex == lineMessageIndex;
    }

    private void renderButtons(PoseStack poseStack, float guiScale, int visibleMessageIndex) {
        poseStack.pushPose();
        poseStack.scale(0.6100F, 0.6100F, 1.0F);

        EDIT_BUTTON.render(visibleMessageIndex, poseStack, guiScale, 16, this.getIconYBasedOnIndex(visibleMessageIndex));
        DELETE_BUTTON.render(visibleMessageIndex, poseStack, guiScale, 1, this.getIconYBasedOnIndex(visibleMessageIndex));

        poseStack.popPose();
    }

    @Inject(method = "handleChatQueueClicked(DD)Z", at = @At(target = "Lnet/minecraft/client/Minecraft;getChatListener()Lnet/minecraft/client/multiplayer/chat/ChatListener;", value = "INVOKE"))
    public void handleIconClick(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
        for (int lineMessageIndex = 0; (lineMessageIndex + chatScrollbarPos) < trimmedMessages.size() && lineMessageIndex < this.getLinesPerPage(); lineMessageIndex++) {
            GuiMessage.Line lineMessage = this.trimmedMessages.get(lineMessageIndex + this.chatScrollbarPos);
            if (!shouldRenderButtons(lineMessage, lineMessageIndex)) {
                continue;
            }

            if (EDIT_BUTTON.isHovered(lineMessageIndex, (float) this.getScale(), 16, this.getIconYBasedOnIndex(lineMessageIndex))) {
                clickAndGetMessage(lineMessage, EDIT_BUTTON);
                break;
            }

            if (DELETE_BUTTON.isHovered(lineMessageIndex, (float) this.getScale(), 1, this.getIconYBasedOnIndex(lineMessageIndex))) {
                clickAndGetMessage(lineMessage, DELETE_BUTTON);
                break;
            }
        }
    }

    private void clickAndGetMessage(GuiMessage.Line lineMessage, ChatButton chatButton) {
        GuiMessage message = MessageHandler.getMessageFromLine(lineMessage);
        chatButton.getOnPress().press(message);
    }

    private int getIconYBasedOnIndex(int messageIndex) {
        return (-15 - (messageIndex * 6) + calculateIconSpacing(messageIndex) + 10);
    }

    private int calculateIconSpacing(int messageIndex) {
        double chatSpacing = this.minecraft.options.chatLineSpacing().get();
        double chatMargin = -8.0 * (chatSpacing + 1.0) + 4.0 * chatSpacing;

        int lineMaxHeight = -messageIndex * this.getLineHeight();
        return (int) ((double) lineMaxHeight + chatMargin);
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/StringSplitter;componentStyleAtWidth(Lnet/minecraft/util/FormattedCharSequence;I)Lnet/minecraft/network/chat/Style;"),
            method = "getClickedComponentStyleAt(DD)Lnet/minecraft/network/chat/Style;",
            index = 1
    )
    public int correctMessageClickPosition(int x) {
        return x - 18;
    }
}
