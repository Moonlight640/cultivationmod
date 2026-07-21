package Moonlight.mod.network.packets.c2s;

import Moonlight.mod.ability.AbilityHandler;
import Moonlight.mod.ability.CultivationAbilities;
import Moonlight.mod.ability.base.Ability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UntriggerAbilityC2S {
    private final ResourceLocation key;

    public UntriggerAbilityC2S(ResourceLocation key) {
        this.key = key;
    }

    public UntriggerAbilityC2S(FriendlyByteBuf buf) {
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

            Ability ability = CultivationAbilities.getValue(this.key);

            if (ability == null) return;

            AbilityHandler.untrigger(player, ability);
        });
        context.setPacketHandled(true);
    }
}