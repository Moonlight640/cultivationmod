package Moonlight.mod.data.capability.playerStats.DelayedTickEvents;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;

public class DelayedTickEvent {
    private final DelayAction action;
    private int delay;
    private boolean save;


    //private final Runnable task;
    //private int delay;
    //private boolean save;

//    public DelayedTickEvent(Runnable task, int delay, @Nullable Boolean save) {
//        this.task = task;
//        this.delay = delay;
//        this.save = save != null ? save : false;
//    }

    public DelayedTickEvent(DelayAction action, int delay, @Nullable Boolean save) {
        this.action = action;
        this.delay = delay;
        this.save = save != null ? save : false;
    }

    public void tick() {
        this.delay--;
    }

    public boolean finished() {
        return this.delay == 0;
    }

    public void run(@Nullable ServerLevel level) {
        //this.task.run();
        this.action.run(level);
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        nbt.putString("type", this.action.getType().name());
        nbt.put("action", this.action.serializeNBT());
        nbt.putInt("delay", this.delay);
        nbt.putBoolean("save", this.save);

        return nbt;
    }

    public static DelayedTickEvent deserializeNBT(CompoundTag nbt) {
        DelayedActionType type = DelayedActionType.valueOf(nbt.getString("type"));
        DelayAction action = type.load(nbt.getCompound("action"));

        return new DelayedTickEvent(
                action,
                nbt.getInt("delay"),
                nbt.getBoolean("save")
        );
    }
}