package Moonlight.mod.network.packets.c2s;

import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class IncreaseOutputC2S {
    public IncreaseOutputC2S() {}
    public IncreaseOutputC2S(FriendlyByteBuf buf) {}
    public void encode(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            assert player != null;

            IPlayerData cap = player.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
            cap.increaseOutput();
            player.sendSystemMessage(Component.literal("Output: " + cap.getOutput()));
        });
        context.setPacketHandled(true);
        return true;
    }
}
