package Moonlight.mod.entity.base;

import Moonlight.mod.ability.base.Ability;
import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import Moonlight.mod.data.capability.playerStats.MartialRealm;
import Moonlight.mod.util.CultivationUtil;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ICultivator {
    boolean hasMeleeAttack();
    boolean hasArms();
    boolean canJump();

    double getBodyExperience();
    double getQiExperience();

    default double getMaxQi() {
        return 0.0F;
    }

    default MartialRealm getRealm() {
        Entity entity = (Entity) this;

        if (!entity.isAddedToWorld()) {
            return CultivationUtil.getRealm(this.getBodyExperience() + this.getQiExperience());
        }
        IPlayerData cap = entity.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
        return CultivationUtil.getRealm((cap.getBodyExperience() + cap.getQiExperience()));
    }

    default @NotNull List<Ability> getCustom() {
        return List.of();
    }

    default List<Ability> getUnlocked() {
        return List.of();
    }

    default void init(IPlayerData data) {
        data.setBodyExperience(this.getBodyExperience());
        data.setQiExperience(this.getQiExperience());
        data.unlockAll(this.getUnlocked());

        if (this.getMaxQi() > 0.0F) {
            data.setMaxQi(this.getMaxQi(), null, null);
        }
        data.setQi(data.getMaxQi());
    }
}
