package dev.sleep.chatoverwrite.common.network.packet;

import dev.sleep.chatoverwrite.client.chat.ClientChatHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class PacketPlayerDisconnected {

    public static FriendlyByteBuf createWritedBuf(UUID disconnectedPlayerUUID) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUUID(disconnectedPlayerUUID);
        return buf;
    }

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        UUID disconnectPlayerUUID = buf.readUUID();
        client.execute(() -> runOnThread(client, handler, responseSender, disconnectPlayerUUID));
    }

    private static void runOnThread(Minecraft client, ClientPacketListener handler, PacketSender responseSender, UUID disconnectPlayerUUID) {
        ClientChatHandler.INSTANCE.removeMessageFromUUID(disconnectPlayerUUID);
    }
}
