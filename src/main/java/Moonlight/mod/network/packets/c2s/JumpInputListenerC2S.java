package Moonlight.mod.network.packets.c2s;

import Moonlight.mod.entity.base.IJumpInputListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class JumpInputListenerC2S {
    private final boolean down;

    public JumpInputListenerC2S(boolean down) {
        this.down = down;
    }

    public JumpInputListenerC2S(FriendlyByteBuf buf) {
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

            if (player.getVehicle() instanceof IJumpInputListener listener) {
                listener.setJump(this.down);
            } else if (player.getFirstPassenger() instanceof IJumpInputListener listener) {
                listener.setJump(this.down);
            }
        });
        context.setPacketHandled(true);
    }
}
