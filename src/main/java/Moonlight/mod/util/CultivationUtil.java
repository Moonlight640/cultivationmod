package Moonlight.mod.util;

import Moonlight.mod.data.capability.playerStats.MartialRealm;

public class CultivationUtil {
    public static double getPower(double experience) {
        return (1.6D + experience / 7500.0D);
    }

    public static double getDefense(double experience) {
        return (1.3D + experience / 7500.0D);
    }

    public static MartialRealm getRealm(double experience) {
        MartialRealm currentRealm = MartialRealm.REGULAR;

        for (MartialRealm realm : MartialRealm.values()) {
            if (MartialRealm.meetsExperienceRequirement(realm, experience)) {
                currentRealm = realm;
            } else {
                break;
            }
        }
        return currentRealm;
    }
}
