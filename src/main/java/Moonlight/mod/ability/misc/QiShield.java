package Moonlight.mod.ability.misc;

import Moonlight.mod.ability.CultivationAbilities;
import Moonlight.mod.ability.MenuType;
import Moonlight.mod.ability.base.Ability;
import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import Moonlight.mod.effects.CultivationEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class QiShield extends Ability implements Ability.IChannelened {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.NONE;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level() instanceof ServerLevel)) return;
        owner.addEffect(new MobEffectInstance(CultivationEffects.STUN.get(), 2, 1, false, false, false));
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        return CultivationAbilities.hasToggled(owner, CultivationAbilities.QI_FLOW.get()) && super.isValid(owner) && (owner instanceof Player player);
    }

    @Override
    public boolean usesHands() {
        return false;
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        return true;
    }

    @Override
    public float getCost(LivingEntity owner) {
        IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
        return 2.0F;
    }

    @Override
    public boolean shouldLog(LivingEntity owner) {
        return true;
    }

    @Override
    public void onStart(LivingEntity owner) {
        IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
        cap.useQi(0.1D);
    }
}
