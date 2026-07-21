package Moonlight.mod;

import Moonlight.mod.ability.CultivationAbilities;
import Moonlight.mod.commands.CultivationModCommands;
import Moonlight.mod.config.ConfigHolder;
import Moonlight.mod.effects.CultivationEffects;
import Moonlight.mod.hud.ClientOverlays;
import Moonlight.mod.items.ModItems;
import Moonlight.mod.network.PacketHandler;
import Moonlight.mod.sounds.CultivationSounds;
import Moonlight.mod.util.ModCreativeModeTabs;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CultivationMod.MODID)
public class CultivationMod {
    public static final String MODID = "cultivation_mod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CultivationMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Mod Added Stuff
        ModLoadingContext ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.SERVER, ConfigHolder.SERVER_SPEC);
        ctx.registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);


        ModItems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        CultivationSounds.SOUNDS.register(modEventBus);
        CultivationEffects.EFFECTS.register(modEventBus);
        CultivationAbilities.ABILITIES.register(modEventBus);

        //
        modEventBus.addListener(CultivationMod::onCommonSetup);
        //modEventBus.addListener(ClientOverlays::register);
        //modEventBus.addListener(CultivationMod::onClientSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static void onCommonSetup(final FMLCommonSetupEvent event) {
//        event.enqueueWork(() -> {
//
//        });

        PacketHandler.register();
    }

    public static void onClientSetup(FMLClientSetupEvent event) {

    }
}
