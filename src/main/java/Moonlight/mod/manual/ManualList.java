package Moonlight.mod.manual;

import Moonlight.mod.CultivationMod;
import net.minecraft.network.chat.Component;

public enum ManualList {
    NO_MANUAL(0.0f, 0.25f, null),
    BASIC_MANUAL(1.0f, 0.25f, "Basic Manual"),
    SHAOLIN_BASIC_MANUAL(0.25f, 1.25f, "Shaolin's Body Arts Manual"),
    HEAVENLY_DEMONS_FIRST_STEP(3.0f, 0.25f, "The First Step to the Heavenly Demon's Martial Arts");

    private final float qiGatheringBoost;
    private final float bodyGatheringBoost;


    private final String description;

    public float getQiGatheringBoost() {
        return this.qiGatheringBoost;
    }

    public float getBodyGatheringBoost() {
        return this.bodyGatheringBoost;
    }

    public String getDescription() {
        return this.description;
    }

    ManualList(float qiGatheringBoost, float bodyGatheringBoost, String description) {
        this.qiGatheringBoost = qiGatheringBoost;
        this.bodyGatheringBoost = bodyGatheringBoost;
        this.description = (description == null) ? "" : description;
    }

    public Component getName() {
        return Component.translatable(String.format("manual.%s.%s", CultivationMod.MODID, this.name().toLowerCase()));
    }
}
// Add Manual Compatibilities.
// e.g
// someone is a regular disciple and are using the basic technique taught to everyone in the sect.
// and becomes an elder/higher position then they are taught a better/stronger/more complete/etc technique
// and I have to make sure which manuals are compatible with one another (e.g Shaolin Manual is not compatible with Demonic or Wudang Manual.
// to go over to those manuals one may have to release all their Qi they accumulated for a fresh start? or suffer Qi Deviation etc etc