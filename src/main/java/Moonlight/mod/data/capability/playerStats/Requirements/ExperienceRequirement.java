package Moonlight.mod.data.capability.playerStats.Requirements;

import Moonlight.mod.data.capability.IPlayerData;
import net.minecraft.network.chat.Component;

public class ExperienceRequirement implements RealmRequirements {
    private final Double requiredExperience;

    public ExperienceRequirement(Double requiredExperience) {
        this.requiredExperience = requiredExperience;
    }

    @Override
    public boolean isMet(IPlayerData playerData) {
        if (this.requiredExperience == null) return false;
        double totalExperience = playerData.getBodyExperience() + playerData.getQiExperience();

        return totalExperience >= this.requiredExperience;
    }

    public boolean isMet(double experience) {
        if (this.requiredExperience == null) return false;
        return  experience >= this.requiredExperience;
    }

    @Override
    public Component getDescription() {
        return Component.literal("Require Experience: " + this.requiredExperience + "xp");
    }
}
