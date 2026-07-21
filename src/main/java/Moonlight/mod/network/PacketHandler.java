package Moonlight.mod.network;

import Moonlight.mod.CultivationMod;
import Moonlight.mod.network.packets.c2s.*;
import Moonlight.mod.network.packets.s2c.SetOverlayMessageS2C;
import Moonlight.mod.network.packets.s2c.SyncPlayerDataS2C;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        INSTANCE = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(CultivationMod.MODID, "packet_handler"))
                .networkProtocolVersion(() -> "1.0.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();


        // Server to Client Packets
        INSTANCE.messageBuilder(SyncPlayerDataS2C.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncPlayerDataS2C::new)
                .encoder(SyncPlayerDataS2C::encode)
                .consumerMainThread(SyncPlayerDataS2C::handle)
                .add();

        INSTANCE.messageBuilder(SetOverlayMessageS2C.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SetOverlayMessageS2C::new)
                .encoder(SetOverlayMessageS2C::encode)
                .consumerMainThread(SetOverlayMessageS2C::handle)
                .add();

        INSTANCE.messageBuilder(TriggerAbilityC2S.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(TriggerAbilityC2S::new)
                .encoder(TriggerAbilityC2S::encode)
                .consumerMainThread(TriggerAbilityC2S::handle)
                .add();







        // Client to Server Packets
        INSTANCE.messageBuilder(GetPlayerDataC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(GetPlayerDataC2S::new)
                .encoder(GetPlayerDataC2S::encode)
                .consumerMainThread(GetPlayerDataC2S::handle)
                .add();

        INSTANCE.messageBuilder(ToggleMeditatingC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ToggleMeditatingC2S::new)
                .encoder(ToggleMeditatingC2S::encode)
                .consumerMainThread(ToggleMeditatingC2S::handle)
                .add();

        INSTANCE.messageBuilder(IncreaseOutputC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(IncreaseOutputC2S::new)
                .encoder(IncreaseOutputC2S::encode)
                .consumerMainThread(IncreaseOutputC2S::handle)
                .add();

        INSTANCE.messageBuilder(DecreaseOutputC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(DecreaseOutputC2S::new)
                .encoder(DecreaseOutputC2S::encode)
                .consumerMainThread(DecreaseOutputC2S::handle)
                .add();

        INSTANCE.messageBuilder(JumpInputListenerC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(JumpInputListenerC2S::new)
                .encoder(JumpInputListenerC2S::encode)
                .consumerMainThread(JumpInputListenerC2S::handle)
                .add();

        INSTANCE.messageBuilder(RightClickInputListenerC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(RightClickInputListenerC2S::new)
                .encoder(RightClickInputListenerC2S::encode)
                .consumerMainThread(RightClickInputListenerC2S::handle)
                .add();

        INSTANCE.messageBuilder(TriggerAbilityC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(TriggerAbilityC2S::new)
                .encoder(TriggerAbilityC2S::encode)
                .consumerMainThread(TriggerAbilityC2S::handle)
                .add();

        INSTANCE.messageBuilder(UnlockAbilityC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(UnlockAbilityC2S::new)
                .encoder(UnlockAbilityC2S::encode)
                .consumerMainThread(UnlockAbilityC2S::handle)
                .add();

        INSTANCE.messageBuilder(UntriggerAbilityC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(UntriggerAbilityC2S::new)
                .encoder(UntriggerAbilityC2S::encode)
                .consumerMainThread(UntriggerAbilityC2S::handle)
                .add();
    }

    public static <PACKET> void broadcast(PACKET packet) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
    }

    public static <PACKET> void sendToClient(PACKET packet, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static <PACKET> void sendTracking(PACKET packet, Entity entity) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity),packet);
    }

    public static <PACKET> void sendToServer(PACKET packet) {
        INSTANCE.sendToServer(packet);
    }
}
