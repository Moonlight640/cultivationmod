package Moonlight.mod.data.capability.playerStats.DelayedTickEvents.Actions;

import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import Moonlight.mod.data.capability.playerStats.DelayedTickEvents.DelayAction;
import Moonlight.mod.data.capability.playerStats.DelayedTickEvents.DelayedActionType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class StopConsumingPillAction implements DelayAction {
    private final UUID playerId;

    public StopConsumingPillAction(UUID playerId) {
        this.playerId = playerId;
    }

    public static StopConsumingPillAction deserializeNBT(CompoundTag nbt) {
        UUID player = nbt.getUUID("player");
        return new StopConsumingPillAction(player);
    }

    @Override
    public void run(ServerLevel level) {
        Player player = level.getPlayerByUUID(playerId);

        if (player == null) return;

        IPlayerData cap = player.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
        cap.setConsumingPill(false);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putUUID("player", playerId);

        return nbt;
    }

    @Override
    public DelayedActionType getType() {
        return DelayedActionType.STOP_CONSUMING_PILL;
    }
}
