package dev.sleep.betterchat.common.network;

import dev.sleep.betterchat.Reference;
import dev.sleep.betterchat.common.network.packet.PacketMessageDeleted;
import dev.sleep.betterchat.common.network.packet.PacketNotifyMessageDelete;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class NetworkManager {

    public static final ResourceLocation DELETE_MESSAGE_PACKET_ID = new ResourceLocation(Reference.MODID, "pressed_delete_button");
    public static final ResourceLocation EDIT_MESSAGE_PACKET_ID = new ResourceLocation(Reference.MODID, "pressed_edit_button");

    public static void registerServerPackets(){
        ServerPlayNetworking.registerGlobalReceiver(DELETE_MESSAGE_PACKET_ID, PacketNotifyMessageDelete::receive);
    }

    public static void registerClientPackets(){
        ClientPlayNetworking.registerGlobalReceiver(DELETE_MESSAGE_PACKET_ID, PacketMessageDeleted::receive);
    }
}
