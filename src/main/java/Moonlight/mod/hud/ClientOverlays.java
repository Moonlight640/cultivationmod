package Moonlight.mod.hud;

import Moonlight.mod.CultivationMod;
import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;


public class ClientOverlays {
////    public static final IGuiOverlay CUSTOM_HEARTS =
////            (gui, graphics, partialTick, width, height) -> {
////
////                Minecraft minecraft = Minecraft.getInstance();
////                if (minecraft.player == null) return;
////
////                RenderFunctions.updateHearts(minecraft.player);
////                RenderFunctions.renderHearts(graphics, minecraft.player, width, height);
////            };
//    public static ResourceLocation TEXTURE = new ResourceLocation(CultivationMod.MODID, "textures/gui/health_bar.png");
//    private static final float SCALE = 1F;
//
//    public static IGuiOverlay OVERLAY = (gui, graphics, partialTick, width, height) -> {
//        Minecraft minecraft = Minecraft.getInstance();
//        if (minecraft.player == null) return;
//
//        if (!minecraft.player.getCapability(DataHandler.INSTANCE).isPresent()) return;
//        IPlayerData cap = minecraft.player.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
//
//        graphics.pose().pushPose();
//        graphics.pose().scale(SCALE, SCALE, SCALE);
//
//        List<Component> above = new ArrayList<>();
//        above.add(Component.translatable(String.format("gui.%s.health_bar_overlay.output", CultivationMod.MODID), Math.round(cap.getOutput() * 100)));
//        above.add(Component.translatable(String.format("gui.%s.health_bar_overlay.experience", CultivationMod.MODID), cap.getBodyExperience() + cap.getQiExperience()));
//
//        int aboveY = 26;
//
//        for (Component line : above) {
//            graphics.drawString(gui.getFont(), line, Math.round(20 * (1.0F / SCALE)), Math.round(aboveY * (1.0F / SCALE)), 16777215);
//            aboveY += minecraft.font.lineHeight - 1;
//        }
//
//        graphics.pose().pushPose();
//
//        if (minecraft.player.getHealth() > 0) {
//            RenderSystem.disableDepthTest();
//            RenderSystem.depthMask(false);
//            RenderSystem.defaultBlendFunc();
//            RenderSystem.setShader(GameRenderer::getPositionTexShader);
//            Vector3f colour = Vec3.fromRGB24(6205439).toVector3f();
//            RenderSystem.setShaderColor(colour.x, colour.y, colour.z, 1.0F);
//
//
//            graphics.blit(TEXTURE, 20, aboveY, 0, 0, 93, 10, 93, 18);
//            float healthWidth = (minecraft.player.getHealth() / minecraft.player.getMaxHealth()) * 94.0F;
//            graphics.blit(TEXTURE, 20, aboveY + 1, 0, 10, (int) healthWidth, 8, 93, 18);
//
//            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//        }
//
//        graphics.pose().popPose();
//
//        RenderSystem.depthMask(true);
//        RenderSystem.enableDepthTest();
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//    };
//
////    @SubscribeEvent
////    public static void register(RegisterGuiOverlaysEvent event) {
////        //event.registerAboveAll("custom_hearts", CUSTOM_HEARTS);
////        event.registerAboveAll("heart_bar", OVERLAY);
////    }
}
