package Moonlight.mod.data.capability.playerStats;

import net.minecraft.nbt.CompoundTag;

public class Stat {
    private double baseValue = 0;
    private double bonusValue = 0;
    private double multiplier = 1;

    public double getValue() {
        return (baseValue + bonusValue) * multiplier;
    }

    public double getBaseValue() {
        return baseValue;
    }

    public double getBonusValue() {
        return bonusValue;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void addBaseValue(Double baseValue) {
        this.baseValue += baseValue;
    }

    public void addBonusValue(Double bonusValue) {
        this.bonusValue += bonusValue;
    }

    public void addMultiplier(Double multiplier) {
        this.multiplier += multiplier;
    }

    public void setBaseValue(Double baseValue) {
        this.baseValue = baseValue;
    }

    public void setBonusValue(Double bonusValue) {
        this.bonusValue = bonusValue;
    }

    public void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putDouble("basevalue", baseValue);
        tag.putDouble("bonusvalue", bonusValue);
        tag.putDouble("multiplier", multiplier);

        return tag;
    }

    public static Stat fromNBT(CompoundTag tag) {
        Stat playerStat = new Stat();

        playerStat.baseValue = tag.getDouble("basevalue");
        playerStat.bonusValue = tag.getDouble("bonusvalue");
        playerStat.multiplier = tag.getDouble("multiplier");

        return playerStat;
    }
}
