package Moonlight.mod.network.packets.s2c;

import Moonlight.mod.client.ClientWrapper;
import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import Moonlight.mod.data.capability.PlayerData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncPlayerDataS2C {
    private final CompoundTag nbt;

    public SyncPlayerDataS2C(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public SyncPlayerDataS2C(FriendlyByteBuf buf) {
        this(buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(this.nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Player player = ClientWrapper.getPlayer();
            assert player != null;

            IPlayerData oldcap = player.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
            IPlayerData newCap = new PlayerData();
            newCap.deserializeNBT(this.nbt);
            oldcap.deserializeNBT(this.nbt);
        }));
        context.setPacketHandled(true);
    }
}
