package dev.sleep.betterchat.common.network.packet;

import dev.sleep.betterchat.client.chat.ClientChatHandler;
import dev.sleep.betterchat.common.chat.EditableChatMessage;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

import java.time.Instant;

public class PacketMessageDeleted {

    public static FriendlyByteBuf createWritedBuf(EditableChatMessage chatMessage) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInstant(chatMessage.getChatMessage().timeStamp());
        return buf;
    }

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        Instant timeStamp = buf.readInstant();
        client.execute(() -> runOnThread(client, handler, responseSender, timeStamp));
    }

    private static void runOnThread(Minecraft client, ClientPacketListener handler, PacketSender responseSender, Instant timeStamp) {
        EditableChatMessage chatMessage = ClientChatHandler.INSTANCE.getEMessageByTimeStamp(timeStamp);
        if(chatMessage == null){
            return;
        }

        ClientChatHandler.INSTANCE.deleteMessage(chatMessage);
    }

}
