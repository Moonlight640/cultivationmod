package Moonlight.mod.network.packets.c2s;

import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleMeditatingC2S {
    public ToggleMeditatingC2S() {}
    public ToggleMeditatingC2S(FriendlyByteBuf buf) {}
    public void encode(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            assert player != null;

            player.sendSystemMessage(Component.literal("Packet Received."));

            IPlayerData cap = player.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
            cap.setMeditating(!cap.isMeditating());
        });
        context.setPacketHandled(true);
        return true;
    }
}
