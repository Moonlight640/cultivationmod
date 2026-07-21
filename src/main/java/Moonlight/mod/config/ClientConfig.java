package Moonlight.mod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public final ForgeConfigSpec.BooleanValue visibleQi;

    public ClientConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Settings").push("settings");
        this.visibleQi = builder.comment("Whether or not Qi Particles are visible")
                .define("visibleQi", true);

        builder.pop();
    }
}
