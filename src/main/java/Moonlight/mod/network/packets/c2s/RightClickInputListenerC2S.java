package Moonlight.mod.network.packets.c2s;

import Moonlight.mod.entity.base.IRightClickInputListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RightClickInputListenerC2S {
    private final boolean down;

    public RightClickInputListenerC2S(boolean down) {
        this.down = down;
    }

    public RightClickInputListenerC2S(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.down);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();

            assert player != null;

            if (player.getVehicle() instanceof IRightClickInputListener listener) {
                listener.setDown(this.down);
            }
        });
        context.setPacketHandled(true);
    }
}