package Moonlight.mod.data.capability.playerStats.DelayedTickEvents;

import Moonlight.mod.data.capability.playerStats.DelayedTickEvents.Actions.StopConsumingPillAction;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Function;

public enum DelayedActionType {
    STOP_CONSUMING_PILL(StopConsumingPillAction::deserializeNBT);

    private final Function<CompoundTag, DelayAction> loader;

    DelayedActionType(Function<CompoundTag, DelayAction> loader) {
        this.loader = loader;
    }

    public DelayAction load(CompoundTag tag) {
        return loader.apply(tag);
    }
}