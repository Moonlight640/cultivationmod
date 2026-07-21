package Moonlight.mod.data.capability.playerStats;

import Moonlight.mod.manual.ManualList;
import net.minecraft.nbt.CompoundTag;

public enum InternalArtData {
    NO_MANUAL("No Manual", null, null, ManualList.NO_MANUAL),
    BASIC_MANUAL("Basic Manual", ManualGrade.COMMON, MartialPath.WANDERER, ManualList.BASIC_MANUAL),
    SHAOLIN_BASIC_MANUAL("Shaolin's Basic Manual", ManualGrade.UNCOMMON, MartialPath.ORTHODOX, ManualList.SHAOLIN_BASIC_MANUAL),
    HEAVENLY_DEMONS_FIRST_STEP("Heavenly Demon's First Step", ManualGrade.DIVINE, MartialPath.DEMONIC, ManualList.HEAVENLY_DEMONS_FIRST_STEP);

    private final String name;
    private final ManualGrade grade;
    private final MartialPath path;
    private final ManualList manualData;

    public String getName() {
        return this.name;
    }

    public ManualGrade getGrade() {
        return this.grade;
    }

    public MartialPath getPath() {
        return this.path;
    }

    public ManualList getManualData() {
        return this.manualData;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putString("name", name);

        return tag;
    }

//    public static InternalArtData fromNBT(CompoundTag tag) {
//        String name = tag.getString("name");
//        TechniqueRank rank = TechniqueRank.values()[tag.getInt("rank")];
//        Float experience = tag.getFloat("experience");
//        ManualGrade grade = ManualGrade.values()[tag.getInt("grade")];
//        MartialPath path = MartialPath.values()[tag.getInt("path")];
//        ManualList manualData = ManualList.valueOf(tag.getString("manualData"));
//
//        return HEAVENLY_DEMONS_FIRST_STEP;
//    }

    InternalArtData(String name, ManualGrade grade, MartialPath path, ManualList manualData) {
        this.name = name;
        this.grade = grade;
        this.path = path;
        this.manualData = manualData;
    }
}