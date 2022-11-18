package dev.sleep.betterchat.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.sleep.betterchat.client.chat.ClientChatHandler;
import dev.sleep.betterchat.client.gui.chat.ChatScreenContainer;
import dev.sleep.betterchat.client.gui.chat.widget.ChatButton;
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

    /**
     * @author KyoSleep
     * @reason We need a way to know if the current FormattedCharSequence is the first entry so we hack "endOfEntry" to "startOfEntry"
     * this helps us to render icon in the correct line.
     */
    @Overwrite
    private void addMessage(Component chatComponent, @Nullable MessageSignature headerSignature, int addedTime, @Nullable GuiMessageTag tag, boolean onlyTrim) {
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

    /**
     * @author KyoSleep
     * @reason Draw Edit/Delete button during message rendering, remove Scroll Indicator (It's ugly) and Message Safety Icon indicator
     * (It's useless and scares people)
     */
    @Overwrite
    public void render(PoseStack poseStack, int tickCount) {
        ChatComponent chatComponent = (ChatComponent) (Object) this;
        ClientChatHandler.reset(false);

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
        renderMessages(poseStack, chatWidthBasedOnScale, chatOpacityFactor, textBackgroundOpacity, chatSpacing, tickCount);

        poseStack.popPose();
    }

    private void renderMessages(PoseStack poseStack, int chatWidthBasedOnScale, double chatOpacityFactor, double textBackgroundOpacity, double chatSpacing, int tickCount) {
        for (int visibleMessageIndex = 0; (visibleMessageIndex + chatScrollbarPos) < this.trimmedMessages.size() && visibleMessageIndex < this.getLinesPerPage(); visibleMessageIndex++) {
            GuiMessage.Line visibleMessage = this.trimmedMessages.get(visibleMessageIndex + this.chatScrollbarPos);
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
            int lineMaxHeight = -visibleMessageIndex * this.getLineHeight();

            float textPositionX = 0.0F;
            int textPositionY = (int) ((double) lineMaxHeight + chatMargin);

            RenderSystem.enableBlend();
            poseStack.pushPose();
            poseStack.translate(0.0, 0.0, 50.0);

            ChatComponent.fill(poseStack, -4, (lineMaxHeight - this.getLineHeight()), chatWidthBasedOnScale + 16, lineMaxHeight, backgroundOpacityColor << 24);
            if (shouldRenderButtons(visibleMessage)) {
                textPositionX = 20.0F;
                renderButtons(poseStack, (float) this.getScale(), visibleMessageIndex, textPositionY);
            }

            this.minecraft.font.drawShadow(poseStack, visibleMessage.content(), textPositionX, (float) textPositionY, 0xFFFFFF + (chatOpacityColor << 24));
            poseStack.popPose();
            RenderSystem.disableBlend();
        }
    }

    private static double getTimeFactor(int i) {
        double d = (double) i / 200.0;
        d = 1.0 - d;
        d *= 10.0;
        d = Mth.clamp(d, 0.0, 1.0);
        d *= d;
        return d;
    }

    private boolean shouldRenderButtons(GuiMessage.Line line) {
        return line.endOfEntry() && ClientChatHandler.isMessageOwner(line.addedTime());
    }

    private void renderButtons(PoseStack poseStack, float guiScale, int visibleMessageIndex, int textPositionY) {
        poseStack.pushPose();
        poseStack.scale(0.6100F, 0.6100F, 1.0F);

        ChatScreenContainer.getEditButton().render(visibleMessageIndex, poseStack, guiScale, 16, (calculateIconSpacing(visibleMessageIndex) + textPositionY) + 10);
        ChatScreenContainer.getDeleteButton().render(visibleMessageIndex, poseStack, guiScale, 1, (calculateIconSpacing(visibleMessageIndex) + textPositionY) + 10);

        poseStack.popPose();
    }

    private int calculateIconSpacing(int messageIndex) {
        return -15 - (messageIndex * 6);
    }

    @Inject(method = "handleChatQueueClicked(DD)Z", at = @At(target = "Lnet/minecraft/client/Minecraft;getChatListener()Lnet/minecraft/client/multiplayer/chat/ChatListener;", value = "INVOKE"))
    public void handleIconClick(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
        ChatButton editButton = ChatScreenContainer.getEditButton();
        ChatButton deleteButton = ChatScreenContainer.getDeleteButton();

        double chatSpacing = this.minecraft.options.chatLineSpacing().get();
        for (int visibleMessageIndex = 0; (visibleMessageIndex + chatScrollbarPos) < trimmedMessages.size() && visibleMessageIndex < this.getLinesPerPage(); visibleMessageIndex++) {
            GuiMessage.Line visibleMessage = this.trimmedMessages.get(visibleMessageIndex + this.chatScrollbarPos);
            double chatMargin = -8.0 * (chatSpacing + 1.0) + 4.0 * chatSpacing;

            int lineMaxHeight = -visibleMessageIndex * this.getLineHeight();
            int textPositionY = (int) ((double) lineMaxHeight + chatMargin);

            if (editButton.isHovered(visibleMessageIndex, (float) this.getScale(), 16, (calculateIconSpacing(visibleMessageIndex) + textPositionY) + 10)) {
                editButton.press(visibleMessage, this.allMessages);
            }

            if (deleteButton.isHovered(visibleMessageIndex, (float) this.getScale(), 1, (calculateIconSpacing(visibleMessageIndex) + textPositionY) + 10)) {
                deleteButton.press(visibleMessage, this.allMessages);
            }
        }
    }

    @Inject(method = "clearMessages(Z)V", at = @At("HEAD"))
    public void clearOwnerList(boolean clearSentMsgHistory, CallbackInfo ci) {
        ClientChatHandler.reset(true);
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/StringSplitter;componentStyleAtWidth(Lnet/minecraft/util/FormattedCharSequence;I)Lnet/minecraft/network/chat/Style;"),
            method = "getClickedComponentStyleAt(DD)Lnet/minecraft/network/chat/Style;",
            index = 1
    )
    public int correctMessageClickPosition(int x) {
        return x - 18;
    }
}
