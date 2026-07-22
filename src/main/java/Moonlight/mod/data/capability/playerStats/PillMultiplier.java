package Moonlight.mod.data.capability.playerStats;

import javax.annotation.Nullable;

public class PillMultiplier {
    private double qiGainMultiplier;
    private double bodyGainMultiplier;

    private double qiExpGainMultiplier;
    private double bodyExpGainMultiplier;

    public PillMultiplier(double qiGainMultiplier, double bodyGainMultiplier, double qiExpGainMultiplier, double bodyExpGainMultiplier) {
        this.qiGainMultiplier = qiGainMultiplier;
        this.bodyGainMultiplier = bodyGainMultiplier;

        this.qiExpGainMultiplier = qiExpGainMultiplier;
        this.bodyExpGainMultiplier = bodyExpGainMultiplier;
    }

    public double getQiGainMultiplier() {
        return this.qiGainMultiplier;
    }
    public double getBodyGainMultiplier() {
        return this.bodyGainMultiplier;
    }
    public double getQiExpGainMultiplier() {
        return this.qiExpGainMultiplier;
    }
    public double getBodyExpGainMultiplier() {
        return this.bodyExpGainMultiplier;
    }



    public void addQiGainMultiplier(double amount) {
        this.qiGainMultiplier += amount;
    }
    public void addBodyGainMultiplier(double amount) {
        this.bodyGainMultiplier += amount;
    }
    public void addQiExpGainMultiplier(double amount) {
        this.qiExpGainMultiplier += amount;
    }
    public void addBodyExpGainMultiplier(double amount) {
        this.bodyExpGainMultiplier += amount;
    }



    public void removeQiGainMultiplier(double amount) {
        this.qiGainMultiplier = Math.max(0, this.qiGainMultiplier - amount);
    }
    public void removeBodyGainMultiplier(double amount) {
        this.bodyGainMultiplier = Math.max(0, this.bodyGainMultiplier - amount);
    }
    public void removeQiExpGainMultiplier(double amount) {
        this.qiExpGainMultiplier = Math.max(0, this.qiExpGainMultiplier - amount);
    }
    public void removeBodyExpGainMultiplier(double amount) {
        this.bodyExpGainMultiplier = Math.max(0, this.bodyExpGainMultiplier - amount);
    }
}
