package Moonlight.mod.data.capability.playerStats.DelayedTickEvents;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public interface DelayAction {
    void run(ServerLevel level);

    CompoundTag serializeNBT();
    DelayedActionType getType();
}
