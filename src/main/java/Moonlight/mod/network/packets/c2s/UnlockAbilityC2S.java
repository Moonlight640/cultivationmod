package Moonlight.mod.network.packets.c2s;

import Moonlight.mod.ability.CultivationAbilities;
import Moonlight.mod.ability.base.Ability;
import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UnlockAbilityC2S {
    private final ResourceLocation key;

    public UnlockAbilityC2S(ResourceLocation key) {
        this.key = key;
    }

    public UnlockAbilityC2S(FriendlyByteBuf buf) {
        this(buf.readResourceLocation());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.key);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();

            assert player != null;

            IPlayerData cap = player.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();

            Ability ability = CultivationAbilities.getValue(this.key);

            if (ability == null) return;

            if (ability.canUnlock(player)) {
//                if (!player.getAbilities().instabuild) {
//
//                }
                cap.unlock(ability);
            }
        });
        context.setPacketHandled(true);
    }
}