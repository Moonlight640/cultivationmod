package Moonlight.mod.network.packets.s2c;

import Moonlight.mod.ability.CultivationAbilities;
import Moonlight.mod.ability.base.Ability;
import Moonlight.mod.client.ability.ClientAbilityHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TriggerAbilityS2C {
    private final ResourceLocation key;

    public TriggerAbilityS2C(ResourceLocation key) {
        this.key = key;
    }

    public TriggerAbilityS2C(FriendlyByteBuf buf) {
        this(buf.readResourceLocation());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.key);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Ability ability = CultivationAbilities.getValue(this.key);
            ClientAbilityHandler.trigger(ability);
        }));

        context.setPacketHandled(true);
    }
}