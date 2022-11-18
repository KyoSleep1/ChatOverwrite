package dev.sleep.betterchat.network.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

public class PacketMessageDeleted {

    public static FriendlyByteBuf createWritedBuf(){
        FriendlyByteBuf buf = PacketByteBufs.create();
        //Write message data
        return buf;
    }

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        client.execute(() -> runOnThread(client, handler, buf, responseSender));
    }

    private static void runOnThread(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
    }

}
