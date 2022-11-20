package dev.sleep.chatoverwrite.common.network;

import dev.sleep.chatoverwrite.Reference;
import dev.sleep.chatoverwrite.common.network.packet.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class NetworkManager {

    public static final ResourceLocation DELETED_MESSAGE_PACKET_ID = new ResourceLocation(Reference.MODID, "message_deleted");
    public static final ResourceLocation EDITED_MESSAGE_PACKET_ID = new ResourceLocation(Reference.MODID, "message_edited");

    public static final ResourceLocation PLAYER_DISCONNECTED_PACKET_ID = new ResourceLocation(Reference.MODID, "player_disconnect");

    public static void registerServerPackets(){
        ServerPlayNetworking.registerGlobalReceiver(DELETED_MESSAGE_PACKET_ID, PacketNotifyMessageDelete::receive);
        ServerPlayNetworking.registerGlobalReceiver(EDITED_MESSAGE_PACKET_ID, PacketNotifyMessageEdited::receive);
    }

    public static void registerClientPackets(){
        ClientPlayNetworking.registerGlobalReceiver(DELETED_MESSAGE_PACKET_ID, PacketMessageDeleted::receive);
        ClientPlayNetworking.registerGlobalReceiver(EDITED_MESSAGE_PACKET_ID, PacketMessageEdited::receive);
        ClientPlayNetworking.registerGlobalReceiver(PLAYER_DISCONNECTED_PACKET_ID, PacketPlayerDisconnected::receive);
    }
}
