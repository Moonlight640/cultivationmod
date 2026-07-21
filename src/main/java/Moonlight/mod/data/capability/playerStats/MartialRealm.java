package Moonlight.mod.data.capability.playerStats;

import Moonlight.mod.CultivationMod;
import Moonlight.mod.config.ConfigHolder;
import Moonlight.mod.config.ServerConfig;
import Moonlight.mod.data.capability.IPlayerData;
import Moonlight.mod.data.capability.playerStats.Requirements.ExperienceRequirement;
import Moonlight.mod.data.capability.playerStats.Requirements.RealmRequirements;
import net.minecraft.network.chat.Component;

import java.util.List;

public enum MartialRealm {
    REGULAR(
            List.of(
                    new ExperienceRequirement(ConfigHolder.SERVER.thirdRateExp.get().doubleValue())
            )),


    THIRD_RATE(
            List.of(
                    new ExperienceRequirement(ConfigHolder.SERVER.secondRateExp.get().doubleValue())
            )),


    SECOND_RATE(
            List.of(
                    new ExperienceRequirement(ConfigHolder.SERVER.semiFirstRateExp.get().doubleValue())
            )),


    SEMI_FIRST_RATE(
            List.of(
                    new ExperienceRequirement(ConfigHolder.SERVER.firstRateExp.get().doubleValue())
            )),


    FIRST_RATE(
            List.of(
                    new ExperienceRequirement(ConfigHolder.SERVER.masterExp.get().doubleValue())
            )),


    MASTER(
            List.of(
                    new ExperienceRequirement(ConfigHolder.SERVER.grandmasterExp.get().doubleValue())
            )),


    GRANDMASTER(
            List.of(
                    new ExperienceRequirement(ConfigHolder.SERVER.transcendentExp.get().doubleValue())
            )),


    TRANSCENDENT(
            List.of(
                    new ExperienceRequirement(null)
            ));


    private final List<RealmRequirements> realmRequirements;

    MartialRealm(List<RealmRequirements> realmRequirements) {
        this.realmRequirements = realmRequirements;
    }

    public List<RealmRequirements> getRequirements() {
        return this.realmRequirements;
    }

    public Component getName() {
        return Component.translatable(String.format("martial_realm.%s.%s", CultivationMod.MODID, this.name().toLowerCase()));
    }

    public MartialRealm getNext() {
        MartialRealm[] martialRealms = MartialRealm.values();
        int index = this.ordinal();
        if (index < martialRealms.length - 1) {
            return martialRealms[index + 1];
        } else {
            return martialRealms[index];
        }
    }

    public boolean canAdvance(IPlayerData playerData) {
        return realmRequirements.stream().allMatch(req -> req.isMet(playerData));
    }

    public static boolean meetsExperienceRequirement(MartialRealm martialRealm, double experience) {
        return martialRealm.getRequirements().stream()
                .filter(ExperienceRequirement.class::isInstance)
                .map(ExperienceRequirement.class::cast)
                .findFirst()
                .map(req -> req.isMet(experience))
                .orElse(false);
    }
}