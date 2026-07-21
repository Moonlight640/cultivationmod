package Moonlight.mod.client;

import Moonlight.mod.CultivationMod;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public class CultivationKeys {
    public static String KEY_CATEGORY_CULTIVATION = String.format("key.category.%s", CultivationMod.MODID);

    public static KeyMapping OPEN_CULTIVATION_MENU = createKeyMapping("open_cultivation_menu",
            InputConstants.KEY_M);


    public static KeyMapping ACTIVATE_QI_SHIELD = createKeyMapping("activate_qi_shield",
            InputConstants.KEY_LSHIFT);
    public static KeyMapping ACTIVATE_QI_FLOW = createKeyMapping("activate_qi_flow",
            InputConstants.KEY_B);

    public static KeyMapping ACTIVATE_INFINITY = createKeyMapping("activate_infinity",
            InputConstants.KEY_I);



    public static KeyMapping INCREASE_OUTPUT = createKeyMapping("increase_output",
            InputConstants.KEY_UP);
    public static KeyMapping DECREASE_OUTPUT = createKeyMapping("decrease_output",
            InputConstants.KEY_DOWN);

    public static KeyMapping TOGGLE_MEDITATION = createKeyMapping("toggle_meditation",
            InputConstants.KEY_C);


    private static KeyMapping createKeyMapping(String name, int keyCode) {
        return new KeyMapping(String.format("key.%s.%s", CultivationMod.MODID, name), keyCode, KEY_CATEGORY_CULTIVATION);
    }
}
