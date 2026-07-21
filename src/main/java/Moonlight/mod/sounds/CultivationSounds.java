package Moonlight.mod.sounds;

import Moonlight.mod.CultivationMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CultivationSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CultivationMod.MODID);

    public static final RegistryObject<SoundEvent> TEST = SOUNDS.register("test", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(CultivationMod.MODID, "test")));
}
