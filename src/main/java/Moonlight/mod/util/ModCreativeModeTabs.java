package Moonlight.mod.util;

import Moonlight.mod.CultivationMod;
import Moonlight.mod.items.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CultivationMod.MODID);

    public static final RegistryObject<CreativeModeTab> CULTIVATION_MOD_TAB = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.WUDANG_PILL.get().getDefaultInstance())
            .title(Component.translatable("creative_mode_tab.cultivation_mod.main"))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.NO_MANUAL.get());
                output.accept(ModItems.BASIC_MANUAL.get());
                output.accept(ModItems.SHAOLIN_BASIC_MANUAL.get());
                output.accept(ModItems.HEAVENLY_DEMONS_FIRST_STEP.get());


                output.accept(ModItems.TEST_PILL.get());
                output.accept(ModItems.MOUNT_HUA_PILL.get());
                output.accept(ModItems.SHAOLIN_PILL.get());
                output.accept(ModItems.WUDANG_PILL.get());
                output.accept(ModItems.BEGGAR_PILL.get());
                output.accept(ModItems.HEAVENLY_DEMON_CULT_PILL.get());


    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
