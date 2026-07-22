package Moonlight.mod.items;

import Moonlight.mod.CultivationMod;
import Moonlight.mod.data.capability.playerStats.InternalArtData;
import Moonlight.mod.items.custom.ManualItem;
import Moonlight.mod.items.custom.PillItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CultivationMod.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
    public static final RegistryObject<Item> HEAVENLY_DEMONS_FIRST_STEP = ITEMS.register("heavenly_demon_cult_manual",
            () -> new ManualItem(new Item.Properties(), InternalArtData.HEAVENLY_DEMONS_FIRST_STEP));

    public static final RegistryObject<Item> SHAOLIN_BASIC_MANUAL = ITEMS.register("shaolin_manual",
            () -> new ManualItem(new Item.Properties(), InternalArtData.SHAOLIN_BASIC_MANUAL));

    public static final RegistryObject<Item> BASIC_MANUAL = ITEMS.register("basic_manual",
            () -> new ManualItem(new Item.Properties(), InternalArtData.BASIC_MANUAL));

    public static final RegistryObject<Item> NO_MANUAL = ITEMS.register("no_manual",
            () -> new ManualItem(new Item.Properties(), InternalArtData.NO_MANUAL));



    public static final RegistryObject<Item> TEST_PILL = ITEMS.register("test_pill",
            () -> new PillItem(new Item.Properties().stacksTo(16).food(new FoodProperties.Builder().alwaysEat().meat().build()),
                    2.0f, 2.0f, 2.0f, 30, true));

    public static final RegistryObject<Item> MOUNT_HUA_PILL = ITEMS.register("mount_hua_pill",
            () -> new PillItem(new Item.Properties().stacksTo(16).food(PillItem.PILL),
                    2.0f, 2.0f, 2.0f, 30, true));

    public static final RegistryObject<Item> SHAOLIN_PILL = ITEMS.register("shaolin_pill",
            () -> new PillItem(new Item.Properties().stacksTo(16).food(PillItem.PILL),
                    2.0f, 2.0f, 2.0f, 30, true));

    public static final RegistryObject<Item> WUDANG_PILL = ITEMS.register("wudang_pill",
            () -> new PillItem(new Item.Properties().stacksTo(16).food(PillItem.PILL),
                    2.0f, 2.0f, 2.0f, 30, true));

    public static final RegistryObject<Item> BEGGAR_PILL = ITEMS.register("beggar_pill",
            () -> new PillItem(new Item.Properties().stacksTo(16).food(PillItem.PILL),
                    2.0f, 2.0f, 2.0f, 30, true));

    public static final RegistryObject<Item> HEAVENLY_DEMON_CULT_PILL = ITEMS.register("heavenly_demon_cult_pill",
            () -> new PillItem(new Item.Properties().stacksTo(16).food(PillItem.PILL),
                    2.0f, 2.0f, 2.0f, 30, true));


    public static final RegistryObject<Item> COPPER_COIN = ITEMS.register("copper_coin",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> SILVER_COIN = ITEMS.register("silver_coin",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> GOLD_COIN = ITEMS.register("gold_coin",
            () -> new Item(new Item.Properties().stacksTo(64)));
}
