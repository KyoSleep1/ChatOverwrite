package dev.sleep.betterchat.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.sleep.betterchat.client.chat.ClientChatHandler;
import dev.sleep.betterchat.client.chat.gui.widget.ChatButton;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private int chatScrollbarPos;

    @Shadow
    private boolean newMessageSinceScroll;

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

    @Shadow
    protected abstract int getTagIconLeft(GuiMessage.Line line);

    private final ChatButton EDIT_BUTTON = new ChatButton(2, 2, 19, 2, 11, 12),
            DELETE_BUTTON = new ChatButton(0, 0, 0, 0, 0, 0);

    /**
     * @author KyoSleep
     * @reason Draw Edit/Delete button during message rendering, also, deletes message safety icon indicator
     * (It's useless and it only scares people...)
     */
    @Overwrite
    public void render(PoseStack poseStack, int i) {
        ChatComponent chatComponent = (ChatComponent) (Object) this;
        int v, u, t, s, r, p;

        if (this.isChatHidden()) {
            return;
        }

        int j = this.getLinesPerPage();
        int k = this.trimmedMessages.size();

        if (k <= 0) {
            return;
        }

        boolean bl = this.isChatFocused();
        float f = (float) this.getScale();
        int l = Mth.ceil((float) chatComponent.getWidth() / f);

        poseStack.pushPose();
        poseStack.translate(2.0, 8.0, 0.0);
        poseStack.scale(f, f, 1.0f);

        double d = this.minecraft.options.chatOpacity().get() * (double) 0.9f + (double) 0.1f;
        double e = this.minecraft.options.textBackgroundOpacity().get();
        double g = this.minecraft.options.chatLineSpacing().get();

        int m = this.getLineHeight();
        double h = -8.0 * (g + 1.0) + 4.0 * g;
        int n = 0;

        for (int o = 0; o + this.chatScrollbarPos < this.trimmedMessages.size() && o < j; ++o) {
            GuiMessage.Line line = this.trimmedMessages.get(o + this.chatScrollbarPos);
            if (line == null || (p = i - line.addedTime()) >= 200 && !bl) {
                continue;
            }

            double q = bl ? 1.0 : ChatComponentMixin.getTimeFactor(p);
            r = (int) (255.0 * q * d);
            s = (int) (255.0 * q * e);

            ++n;
            if (r <= 3) {
                continue;
            }

            u = -o * m;
            v = (int) ((double) u + h);

            poseStack.pushPose();

            poseStack.translate(0.0, 0.0, 50.0);
            ChatComponent.fill(poseStack, -4, u - m, l + 8, u, s << 24);

            int x = getTagIconLeft(line);
            int y = v + this.minecraft.font.lineHeight;

            this.renderButtons(line, poseStack, x, y);
            RenderSystem.enableBlend();

            poseStack.translate(0.0, 0.0, 50.0);
            this.minecraft.font.drawShadow(poseStack, line.content(), 0.0f, (float) v, 0xFFFFFF + (r << 24));

            RenderSystem.disableBlend();
            poseStack.popPose();
        }

        long z = this.minecraft.getChatListener().queueSize();
        if (z > 0L) {
            p = (int) (128.0 * d);
            int aa = (int) (255.0 * e);

            poseStack.pushPose();
            poseStack.translate(0.0, 0.0, 50.0);

            ChatComponent.fill(poseStack, -2, 0, l + 4, 9, aa << 24);
            RenderSystem.enableBlend();

            poseStack.translate(0.0, 0.0, 50.0);
            this.minecraft.font.drawShadow(poseStack, Component.translatable("chat.queue", z), 0.0f, 1.0f, 0xFFFFFF + (p << 24));

            poseStack.popPose();
            RenderSystem.disableBlend();
        }

        if (bl) {
            p = this.getLineHeight();

            int aa = k * p;
            int ab = n * p;

            r = this.chatScrollbarPos * ab / k;
            s = ab * ab / aa;

            if (aa != ab) {
                t = r > 0 ? 170 : 96;
                u = this.newMessageSinceScroll ? 0xCC3333 : 0x3333AA;
                v = l + 4;

                ChatComponent.fill(poseStack, v, -r, v + 2, -r - s, u + (t << 24));
                ChatComponent.fill(poseStack, v + 2, -r, v + 1, -r - s, 0xCCCCCC + (t << 24));
            }
        }

        poseStack.popPose();
    }

    private void renderButtons(GuiMessage.Line line, PoseStack poseStack, int positionX, int positionY) {
        String legibleText = this.getLegibleText(line.content());

        if (!ClientChatHandler.isMessageOwner(legibleText)) {
            return;
        }


        poseStack.pushPose();
        poseStack.scale(0.7F, 0.7F, 0.7F);

        renderEditButton(poseStack, positionX, positionY);
        renderDeleteButton(poseStack, positionX, positionY);

        poseStack.popPose();
    }

    private void renderEditButton(PoseStack poseStack, int positionX, int positionY) {
        EDIT_BUTTON.render(poseStack, positionX, positionY - (EDIT_BUTTON.getHeight()) - 1, false);
    }

    private void renderDeleteButton(PoseStack poseStack, int positionX, int positionY) {
        //DELETE_BUTTON.render(poseStack, positionX, positionY - (DELETE_BUTTON.getHeight() - 1), false);
    }

    private String getLegibleText(FormattedCharSequence charSequence) {
        StringBuilder stringBuilder = new StringBuilder();

        charSequence.accept((index, style, codePoints) -> {
            stringBuilder.appendCodePoint(codePoints);
            return true;
        });

        return stringBuilder.toString();
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
