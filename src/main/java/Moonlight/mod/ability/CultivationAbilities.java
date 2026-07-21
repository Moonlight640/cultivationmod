package Moonlight.mod.ability;

import Moonlight.mod.CultivationMod;
import Moonlight.mod.ability.base.Ability;
import Moonlight.mod.ability.misc.QiFlow;
import Moonlight.mod.ability.misc.QiShield;
import Moonlight.mod.ability.test.Infinity;
import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import Moonlight.mod.entity.base.ICultivator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CultivationAbilities {
    public static DeferredRegister<Ability> ABILITIES = DeferredRegister.create(
            new ResourceLocation(CultivationMod.MODID, "ability"), CultivationMod.MODID);
    public static Supplier<IForgeRegistry<Ability>> ABILITY_REGISTRY =
            ABILITIES.makeRegistry(RegistryBuilder::new);


    public static RegistryObject<Ability> QI_FLOW = ABILITIES.register("qi_flow", QiFlow::new);
    public static RegistryObject<Ability> QI_SHIELD = ABILITIES.register("qi_shield", QiShield::new);


    public static RegistryObject<Ability> INFINITY = ABILITIES.register("infinity", Infinity::new);














    public static String getName(Ability ability) {
        return getKey(ability).getPath();
    }

    public static ResourceLocation getKey(Ability ability) {
        return ABILITY_REGISTRY.get().getKey(ability);
    }

    public static Ability getValue(ResourceLocation key) {
        return ABILITY_REGISTRY.get().getValue(key);
    }

    public static boolean hasToggled(LivingEntity owner, Ability ability) {
        if (!owner.getCapability(DataHandler.INSTANCE).isPresent()) return false;
        IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
        return cap.hasToggled(ability);
    }

    public static Set<Ability> getToggled(LivingEntity owner) {
        if (!owner.getCapability(DataHandler.INSTANCE).isPresent()) return Set.of();
        IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getToggled();
    }

    public static boolean isChanneling(LivingEntity owner, Ability ability) {
        if (!owner.getCapability(DataHandler.INSTANCE).isPresent()) return false;
        IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
        return cap.isChanneling(ability);
    }

    public static List<Ability> getAbilities() {
        return ABILITIES.getEntries().stream()
                .map(RegistryObject::get)
                .collect(Collectors.toList());
    }

    public static List<Ability> getAbilities(LivingEntity owner) {
        Set<Ability> abilities = new LinkedHashSet<>(List.of());

        IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();

        if (owner instanceof ICultivator cultivator) {
            abilities.addAll(cultivator.getCustom());
        }

        for (RegistryObject<Ability> entry : ABILITIES.getEntries()) {
            Ability ability = entry.get();

            if (!ability.isInternalArt() || ability.getCost(owner) == 0) {
                abilities.add(ability);
            }
        }

        abilities.removeIf(ability -> !ability.isValid(owner) && !(owner instanceof ICultivator cultivator && cultivator.getCustom().contains(ability)));
        return new ArrayList<>(abilities);
    }
}
