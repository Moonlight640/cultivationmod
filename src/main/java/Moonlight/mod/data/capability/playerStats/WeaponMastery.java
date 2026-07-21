package Moonlight.mod.data.capability.playerStats;

import net.minecraft.nbt.CompoundTag;

import java.util.EnumMap;

public class WeaponMastery {
    private MasteryRank rank;
    private float experience;
    private float totalExperience;



    public float getExperience() {
        return experience;
    }

    public MasteryRank getRank() {
        return rank;
    }

    public float getTotalExperience() {
        return totalExperience;
    }

    public void setExperience(float experience) {
        this.experience = experience;
    }

    public void addExperience(float experience) {
        if (this.experience + experience > this.totalExperience) {
            //TODO: Trigger rank update/upgrade
            // experience = this.totalExperience - this.experience;
        }

        this.experience += experience;
    }

    public void setRank(MasteryRank rank) {
        this.rank = rank;
    }

    public void setTotalExperience(float totalExperience) {
        this.totalExperience = totalExperience;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putInt("rank", rank.ordinal());
        tag.putFloat("experience", experience);
        tag.putFloat("totalExperience", totalExperience);

        return tag;
    }

    public static WeaponMastery fromNBT(CompoundTag tag) {
        WeaponMastery mastery = new WeaponMastery();

        mastery.rank = MasteryRank.values()[tag.getInt("rank")];
        mastery.experience = tag.getFloat("experience");
        mastery.totalExperience = tag.getFloat("totalExperience");

        return mastery;
    }
}
