package dev.sleep.betterchat.common.network.packet;

import dev.sleep.betterchat.client.chat.ClientChatHandler;
import dev.sleep.betterchat.common.chat.EditableChatMessage;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class PacketMessageEdited {

    public static FriendlyByteBuf createWritedBuf(EditableChatMessage chatMessage, Component content) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInstant(chatMessage.getChatMessage().timeStamp());
        buf.writeComponent(content);

        return buf;
    }

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        Instant timeStamp = buf.readInstant();
        Component content = buf.readComponent();

        client.execute(() -> runOnThread(client, handler, responseSender, timeStamp, content));
    }

    private static void runOnThread(Minecraft client, ClientPacketListener handler, PacketSender responseSender, Instant timeStamp, Component content) {
        EditableChatMessage chatMessage = ClientChatHandler.INSTANCE.getEMessageByTimeStamp(timeStamp);
        if (chatMessage == null) {
            return;
        }

        UUID senderUUID = chatMessage.getChatMessage().signer().profileId();
        Player senderPlayer = Objects.requireNonNull(Minecraft.getInstance().player).getCommandSenderWorld().getPlayerByUUID(senderUUID);

        if(senderPlayer == null){
            return;
        }

        Component component = ChatType.bind(ChatType.CHAT, senderPlayer).decorate(content);
        ClientChatHandler.INSTANCE.editMessage(chatMessage, component);
    }
}
