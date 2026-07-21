package Moonlight.mod.data;

import Moonlight.mod.CultivationMod;
import Moonlight.mod.data.capability.IPlayerData;
import Moonlight.mod.data.capability.PlayerData;
import Moonlight.mod.network.PacketHandler;
import Moonlight.mod.network.packets.s2c.SyncPlayerDataS2C;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = CultivationMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DataHandler {
    public static Capability<IPlayerData> INSTANCE = CapabilityManager.get(
            new CapabilityToken<>() {});


    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player player = event.getEntity();

        original.reviveCaps();

        IPlayerData oldCap = original.getCapability(INSTANCE).resolve().orElseThrow();
        IPlayerData newCap = player.getCapability(INSTANCE).resolve().orElseThrow();

        newCap.deserializeNBT(oldCap.serializeNBT());

        if (event.isWasDeath()) {
            if (!player.level().isClientSide) {
                PacketHandler.sendToClient(new SyncPlayerDataS2C(newCap.serializeNBT()), (ServerPlayer) player);
            }
        }
        original.invalidateCaps();
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity entity) {
            if (entity instanceof Player) {
                if (!event.getObject().getCapability(INSTANCE).isPresent()) {
                    PlayerDataProvider provider = new PlayerDataProvider();
                    IPlayerData cap = provider.getCapability(INSTANCE).resolve().orElseThrow();
                    cap.init(entity);
                    event.addCapability(PlayerDataProvider.IDENTIFIER, provider);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            IPlayerData cap = player.getCapability(INSTANCE).resolve().orElseThrow();
            PacketHandler.sendToClient(new SyncPlayerDataS2C(cap.serializeNBT()), player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            IPlayerData cap = player.getCapability(INSTANCE).resolve().orElseThrow();
            PacketHandler.sendToClient(new SyncPlayerDataS2C(cap.serializeNBT()), player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            IPlayerData cap = player.getCapability(INSTANCE).resolve().orElseThrow();
            PacketHandler.sendToClient(new SyncPlayerDataS2C(cap.serializeNBT()), player);
        }
    }
    

    public static class PlayerDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private static ResourceLocation IDENTIFIER = new ResourceLocation(CultivationMod.MODID, "murim_player_data");

        private IPlayerData cap = null;
        private final LazyOptional<IPlayerData> optional = LazyOptional.of(this::create);

        private IPlayerData create() {
            if (this.cap == null) {
                this.cap = new PlayerData();
            }
            return this.cap;
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return cap == INSTANCE ? this.optional.cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.create().serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.create().deserializeNBT(nbt);
        }
    }
}
