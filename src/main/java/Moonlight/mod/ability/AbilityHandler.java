package Moonlight.mod.ability;

import Moonlight.mod.ability.base.Ability;
import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;

public class AbilityHandler {
    public static void untrigger(LivingEntity owner, Ability ability) {
        owner.getCapability(DataHandler.INSTANCE).ifPresent(cap -> {
            if (ability.getActivationType(owner) == Ability.ActivationType.TOGGLED) {
                if (cap.hasToggled(ability)) {
                    cap.toggle(ability);
                }
            } else if (ability.getActivationType(owner) == Ability.ActivationType.CHANNELED) {
                if (cap.isChanneling(ability)) {
                    cap.channel(ability);
                }
            }
        });
    }

    public static Ability.Status trigger(LivingEntity owner, Ability ability) {
        IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElse(null);
        if (cap == null) return Ability.Status.FAILURE;
        Ability.Status status = ability.isTriggerable(owner);

        if (ability.getActivationType(owner) == Ability.ActivationType.INSTANT) {
            if (status == Ability.Status.SUCCESS) {
                ability.charge(owner);
                ability.addDuration(owner);
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
                ability.run(owner);
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
            }
        } else if (ability.getActivationType(owner) == Ability.ActivationType.TOGGLED) {
            if (status == Ability.Status.SUCCESS || (status == Ability.Status.QI && ability instanceof Ability.IAttack)) {
                ability.addDuration(owner);
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
                cap.toggle(ability);
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
            }
            return status;
        } else if (ability.getActivationType(owner) == Ability.ActivationType.CHANNELED) {
            if (status == Ability.Status.SUCCESS || (status == Ability.Status.QI && ability instanceof Ability.IAttack)) {
                ability.addDuration(owner);
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
                cap.channel(ability);
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
            }
            return status;
        }
        return status;
    }
}
