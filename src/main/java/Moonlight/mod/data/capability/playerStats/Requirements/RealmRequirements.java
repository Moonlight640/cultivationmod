package Moonlight.mod.data.capability.playerStats.Requirements;

import Moonlight.mod.data.capability.IPlayerData;
import net.minecraft.network.chat.Component;

public interface RealmRequirements {
    boolean isMet(IPlayerData playerData);

    Component getDescription();
}
