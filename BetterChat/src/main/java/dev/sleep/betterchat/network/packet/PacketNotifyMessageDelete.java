package dev.sleep.betterchat.network.packet;

import dev.sleep.betterchat.network.NetworkManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class PacketNotifyMessageDelete {

    public static void receive(MinecraftServer server, ServerPlayer serverPlayer, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        server.execute(() -> runOnThread(server));
    }

    private static void runOnThread(MinecraftServer server) {
        for (ServerPlayer trackingServerPlayer : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(trackingServerPlayer, NetworkManager.DELETE_MESSAGE_PACKET_ID, PacketByteBufs.create());
        }
    }
}
