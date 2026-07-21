package Moonlight.mod.data.capability.playerStats;

public enum PotentialGrade {
    DEFECTIVE      (50),
    BELOW_AVERAGE (750),
    AVERAGE       (2500),
    ABOVE_AVERAGE (5000),
    GENIUS        (15000),
    HEAVENLY      (35000),
    MONSTROUS     (Integer.MAX_VALUE);

    private final int potentialCap;

    PotentialGrade(int potentialCap) {
        this.potentialCap = potentialCap;
    }

    public int getPotentialCap() {
        return potentialCap;
    }
}