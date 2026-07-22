package Moonlight.mod.hud;

import Moonlight.mod.CultivationMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CultivationMod.MODID, value = Dist.CLIENT)
public class ModHudEvents {
    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay().id().equals(VanillaGuiOverlay.PLAYER_HEALTH.id())) {
            //event.setCanceled(true);
        }
    }
}
