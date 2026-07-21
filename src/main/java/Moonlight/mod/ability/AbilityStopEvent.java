package Moonlight.mod.ability;

import Moonlight.mod.ability.base.Ability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

public class AbilityStopEvent extends LivingEvent {
    private final Ability ability;

    public AbilityStopEvent(LivingEntity entity, Ability ability) {
        super(entity);

        this.ability = ability;
    }

    public Ability getAbility() {
        return this.ability;
    }
}
