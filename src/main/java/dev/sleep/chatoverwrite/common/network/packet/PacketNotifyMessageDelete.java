package dev.sleep.chatoverwrite.common.network.packet;

import dev.sleep.chatoverwrite.common.chat.EditableChatMessage;
import dev.sleep.chatoverwrite.common.network.NetworkManager;
import dev.sleep.chatoverwrite.server.chat.ServerChatHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.time.Instant;

public class PacketNotifyMessageDelete {

    public static FriendlyByteBuf createWritedBuf(EditableChatMessage chatMessage) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInstant(chatMessage.getChatMessage().timeStamp());
        return buf;
    }

    public static void receive(MinecraftServer server, ServerPlayer serverPlayer, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        Instant timeStamp = buf.readInstant();
        server.execute(() -> runOnThread(server, serverPlayer, timeStamp));
    }

    private static void runOnThread(MinecraftServer server, ServerPlayer serverPlayer, Instant timeStamp) {
        EditableChatMessage chatMessage = ServerChatHandler.INSTANCE.getEMessageByTimeStamp(timeStamp);
        if (chatMessage == null || !ServerChatHandler.INSTANCE.isMessageOwner(chatMessage, serverPlayer)) {
            return;
        }

        ServerChatHandler.INSTANCE.deleteMessage(chatMessage);
        for (ServerPlayer trackingServerPlayer : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(trackingServerPlayer, NetworkManager.DELETED_MESSAGE_PACKET_ID, PacketMessageDeleted.createWritedBuf(chatMessage));
        }
    }
}
