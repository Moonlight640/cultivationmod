package Moonlight.mod.network.packets.c2s;

import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GetPlayerDataC2S {
    public GetPlayerDataC2S() {}
    public GetPlayerDataC2S(FriendlyByteBuf buf) {}
    public void encode(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            assert player != null;

            IPlayerData cap = player.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
            player.sendSystemMessage(Component.literal(
                    "Sect: " + cap.getSect()
                    + "\nCurrent Qi: " + cap.getCurrentQi()
                    + "\nMax Qi: " + cap.getMaxQi()
                    + "\nManual: " + cap.getInternalArtName()
                    + "\n\nCurrent Realm: " + cap.getCurrentRealm().getName().getString()
                    + "\nBody Experience: " + cap.getBodyExperience()
                    + "\nQi Experience: " + cap.getQiExperience()
                    + "\nTotal Experience: " + (cap.getBodyExperience() + cap.getQiExperience())
                    + "\n\nQi Talent: " + cap.getQiTalent()
                    + "\nQi Potential: " + cap.getQiPotential()
                    + "\nBody Talent: " + cap.getBodyTalent()
                    + "\nBody Potential: " + cap.getBodyPotential()
            ));
        });
        return true;
    }
}
