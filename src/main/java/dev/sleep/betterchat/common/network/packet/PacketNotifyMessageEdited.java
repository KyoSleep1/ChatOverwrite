package dev.sleep.betterchat.common.network.packet;

import dev.sleep.betterchat.common.chat.EditableChatMessage;
import dev.sleep.betterchat.common.network.NetworkManager;
import dev.sleep.betterchat.server.chat.ServerChatHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatMessageContent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.time.Instant;

public class PacketNotifyMessageEdited {

    public static FriendlyByteBuf createWritedBuf(EditableChatMessage chatMessage, ChatMessageContent chatMessageContent) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInstant(chatMessage.getChatMessage().timeStamp());
        buf.writeComponent(chatMessageContent.decorated());
        return buf;
    }

    public static void receive(MinecraftServer server, ServerPlayer serverPlayer, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        Instant timeStamp = buf.readInstant();
        Component content = buf.readComponent();
        server.execute(() -> runOnThread(server, serverPlayer, timeStamp, content));
    }

    private static void runOnThread(MinecraftServer server, ServerPlayer serverPlayer, Instant timeStamp, Component content) {
        EditableChatMessage oldChatMessage = ServerChatHandler.INSTANCE.getEMessageByTimeStamp(timeStamp);
        if (oldChatMessage == null || !ServerChatHandler.INSTANCE.isMessageOwner(oldChatMessage, serverPlayer)) {
            return;
        }

        for (ServerPlayer trackingServerPlayer : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(trackingServerPlayer, NetworkManager.EDITED_MESSAGE_PACKET_ID, PacketMessageEdited.createWritedBuf(oldChatMessage, content));
        }
    }
}
