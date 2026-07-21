package Moonlight.mod.effects;

import Moonlight.mod.CultivationMod;
import Moonlight.mod.effects.base.CultivationEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CultivationEffects {
    public static DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, CultivationMod.MODID);

    public static RegistryObject<MobEffect> STUN = EFFECTS.register("stun", () -> new CultivationEffect(MobEffectCategory.NEUTRAL, 0xFFFFFF));
}
