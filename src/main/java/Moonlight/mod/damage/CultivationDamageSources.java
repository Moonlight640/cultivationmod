package Moonlight.mod.damage;

import Moonlight.mod.CultivationMod;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class CultivationDamageSources {
    public static final ResourceKey<DamageType> QI = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CultivationMod.MODID, "qi"));




    public static CultivationDamageSource qiAttack(LivingEntity source) { // @Nullable Ability ability
        RegistryAccess registryAccess = source.level().registryAccess();
        Registry<DamageType> types = registryAccess.registryOrThrow(Registries.DAMAGE_TYPE);
        return new CultivationDamageSource(types.getHolderOrThrow(QI), source, null); //, ability
    }





    public static class CultivationDamageSource extends DamageSource {
        //private final @Nullable Ability ability;

        public CultivationDamageSource(Holder.Reference<DamageType> holderOrThrow, Entity source, @Nullable LivingEntity indirect) {//@Nullable Ability ability
            super(holderOrThrow, source, indirect);

            //this.ability = ability;
        }

        public CultivationDamageSource(Holder.Reference<DamageType> holderOrThrow, @Nullable LivingEntity mob) { //@Nullable Ability ability
            super(holderOrThrow, mob);

            //this.ability = ability;
        }
        
        //@Nullable
        //public Ability getAbility() {
            //return this.ability;
        //}
    }
}
