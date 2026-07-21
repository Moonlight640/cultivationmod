package Moonlight.mod.network.packets.s2c;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetOverlayMessageS2C {
    private final Component component;
    private final boolean animate;

    public SetOverlayMessageS2C(Component component, boolean animate) {
        this.component = component;
        this.animate = animate;
    }

    public SetOverlayMessageS2C(FriendlyByteBuf buf) {
        this(buf.readComponent(), buf.readBoolean());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeComponent(this.component);
        buf.writeBoolean(this.animate);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.gui.setOverlayMessage(this.component, this.animate);
        }));
        context.setPacketHandled(true);
    }
}
