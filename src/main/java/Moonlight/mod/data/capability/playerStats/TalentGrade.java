package Moonlight.mod.data.capability.playerStats;

public enum TalentGrade {

    DEFECTIVE      (0.25F),
    BELOW_AVERAGE (0.60F),
    AVERAGE       (1.00F),
    ABOVE_AVERAGE (1.30F),
    GENIUS        (1.75F),
    HEAVENLY      (2.25F),
    MONSTROUS     (3.00F);

    private final float growthMultiplier;

    TalentGrade(float growthMultiplier) {
        this.growthMultiplier = growthMultiplier;
    }

    public float getGrowthMultiplier() {
        return growthMultiplier;
    }
}