package Moonlight.mod.util;

import Moonlight.mod.CultivationMod;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class PlayerUtil {
    public static void giveAdvancement(ServerPlayer player, String name) {
        MinecraftServer server = player.getServer();
        assert server != null;
        Advancement advancement = server.getAdvancements().getAdvancement(new ResourceLocation(CultivationMod.MODID,
                String.format("%s/%s", CultivationMod.MODID, name)));

        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);

            if (!progress.isDone()) {
                for (String criteria : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(advancement, criteria);
                }
            }
        }
    }

    public static boolean hasAdvancement(ServerPlayer player, String name) {
        MinecraftServer server = player.getServer();
        if (server == null) return false;

        Advancement advancement = server.getAdvancements().getAdvancement(new ResourceLocation(
                CultivationMod.MODID,
                String.format("%s/%s", CultivationMod.MODID, name)
        ));

        if (advancement == null) return false;

        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        return progress.isDone();
    }

    public static void removeAdvancement(ServerPlayer player, String name) {
        MinecraftServer server = player.getServer();
        assert server != null;
        Advancement advancement = server.getAdvancements().getAdvancement(new ResourceLocation(
                CultivationMod.MODID,
                String.format("%s/%s", CultivationMod.MODID, name)
        ));

        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);

            if (progress.isDone()) {
                for (String criteria : progress.getCompletedCriteria()) {
                    player.getAdvancements().revoke(advancement, criteria);
                }
            }
        }
    }
}
