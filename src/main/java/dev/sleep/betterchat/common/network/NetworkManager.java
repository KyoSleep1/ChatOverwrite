package dev.sleep.betterchat.common.network;

import dev.sleep.betterchat.Reference;
import dev.sleep.betterchat.common.network.packet.PacketMessageDeleted;
import dev.sleep.betterchat.common.network.packet.PacketNotifyMessageDelete;
import dev.sleep.betterchat.common.network.packet.PacketPlayerDisconnected;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class NetworkManager {

    public static final ResourceLocation DELETED_MESSAGE_PACKET_ID = new ResourceLocation(Reference.MODID, "message_deleted");
    public static final ResourceLocation EDITED_MESSAGE_PACKET_ID = new ResourceLocation(Reference.MODID, "message_edited");

    public static final ResourceLocation PLAYER_DISCONNECTED_PACKET_ID = new ResourceLocation(Reference.MODID, "player_disconnect");

    public static void registerServerPackets(){
        ServerPlayNetworking.registerGlobalReceiver(DELETED_MESSAGE_PACKET_ID, PacketNotifyMessageDelete::receive);
    }

    public static void registerClientPackets(){
        ClientPlayNetworking.registerGlobalReceiver(DELETED_MESSAGE_PACKET_ID, PacketMessageDeleted::receive);
        ClientPlayNetworking.registerGlobalReceiver(PLAYER_DISCONNECTED_PACKET_ID, PacketPlayerDisconnected::receive);
    }
}
