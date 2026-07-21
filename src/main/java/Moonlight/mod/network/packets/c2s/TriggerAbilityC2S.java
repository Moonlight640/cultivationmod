package Moonlight.mod.network.packets.c2s;

import Moonlight.mod.CultivationMod;
import Moonlight.mod.ability.AbilityHandler;
import Moonlight.mod.ability.CultivationAbilities;
import Moonlight.mod.ability.base.Ability;
import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import Moonlight.mod.network.PacketHandler;
import Moonlight.mod.network.packets.s2c.SetOverlayMessageS2C;
import Moonlight.mod.network.packets.s2c.TriggerAbilityS2C;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TriggerAbilityC2S {
    private final ResourceLocation key;

    public TriggerAbilityC2S(ResourceLocation key) {
        this.key = key;
    }

    public TriggerAbilityC2S(FriendlyByteBuf buf) {
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

            Ability.Status status;
            Ability ability = CultivationAbilities.getValue(this.key);

            if ((status = AbilityHandler.trigger(player, ability)) == Ability.Status.SUCCESS) {
                PacketHandler.sendToClient(new TriggerAbilityS2C(CultivationAbilities.getKey(ability)), player);
            } else {

                IPlayerData cap = player.getCapability(DataHandler.INSTANCE).resolve().orElse(null);
                if (cap == null) return;

                switch (status) {
                    case QI ->
                            PacketHandler.sendToClient(new SetOverlayMessageS2C(Component.translatable(String.format("ability.%s.fail.qi", CultivationMod.MODID)),
                                    false), player);
                    case COOLDOWN ->
                            PacketHandler.sendToClient(new SetOverlayMessageS2C(Component.translatable(String.format("ability.%s.fail.cooldown", CultivationMod.MODID),
                                    Math.max(1, cap.getRemainingCooldown(ability) / 20)), false), player);
                    case DISABLE ->
                            PacketHandler.sendToClient(new SetOverlayMessageS2C(Component.translatable(String.format("ability.%s.fail.disable", CultivationMod.MODID)),
                                    false), player);
                    case FAILURE ->
                            PacketHandler.sendToClient(new SetOverlayMessageS2C(Component.translatable(String.format("ability.%s.fail.failure", CultivationMod.MODID)),
                                    false), player);
                    case EMPTYINV ->
                            PacketHandler.sendToClient(new SetOverlayMessageS2C(Component.translatable(String.format("ability.%s.fail.emptyinv", CultivationMod.MODID)),
                                    false), player);
                    case UNUSABLE ->
                    {}//PacketHandler.sendToClient(Component.translatable(String.format("ability.%s.fail.unusable", CultivationMod.MODID)), sender);
                    case SUCCESS ->
                    {}
                }
            }
        });
        context.setPacketHandled(true);
    }
}
