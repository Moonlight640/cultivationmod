package Moonlight.mod.client;

import Moonlight.mod.CultivationMod;
import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import Moonlight.mod.hud.ClientOverlays;
import Moonlight.mod.network.PacketHandler;
import Moonlight.mod.network.packets.c2s.DecreaseOutputC2S;
import Moonlight.mod.network.packets.c2s.GetPlayerDataC2S;
import Moonlight.mod.network.packets.c2s.IncreaseOutputC2S;
import Moonlight.mod.network.packets.c2s.ToggleMeditatingC2S;
import Moonlight.mod.util.Keybinding;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = CultivationMod.MODID, value = Dist.CLIENT)
    public static class ClientForgeEvents {
//        @SubscribeEvent
//        public static void onKeyInput(InputEvent.Key event) {
//             if (Minecraft.getInstance().player != null) {
//                 if (Keybinding.TEST_STATS_KEY.consumeClick()) {
//                     Minecraft.getInstance().player.sendSystemMessage(Component.literal("Key Pressed"));
//                     PacketHandler.sendToServer(new GetPlayerDataC2S());
//                 }

//                 if (Keybinding.MEDITATION_KEY.consumeClick()) {
//                     Player player = Minecraft.getInstance().player;
//                     IPlayerData cap = player.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
//
//                     if (!cap.getInternalArtName().equals("No Manual")) {
//                         //player.sendSystemMessage(Component.literal("Meditation Key Pressed"));
//                         PacketHandler.sendToServer(new ToggleMeditatingC2S());
//                     } else {
//                         player.sendSystemMessage(Component.literal("Player has not learnt any manual. Therefore are unable to meditate"));
//                     }
//                 }
//
//
//                 if (Keybinding.INCREASE_OUTPUT_KEY.consumeClick()) {
//                     PacketHandler.sendToServer(new IncreaseOutputC2S());
//                 }
//
//                 if (Keybinding.DECREASE_OUTPUT_KEY.consumeClick()) {
//                     PacketHandler.sendToServer(new DecreaseOutputC2S());
//                 }
//             }
//        }
    }

    @Mod.EventBusSubscriber(modid = CultivationMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(CultivationKeys.ACTIVATE_QI_SHIELD);
            event.register(CultivationKeys.ACTIVATE_QI_FLOW);
            event.register(CultivationKeys.INCREASE_OUTPUT);
            event.register(CultivationKeys.DECREASE_OUTPUT);
            event.register(CultivationKeys.TOGGLE_MEDITATION);
            event.register(CultivationKeys.OPEN_CULTIVATION_MENU);
        }

//        @SubscribeEvent
//        public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
//            event.registerAboveAll("health_bar", ClientOverlays.OVERLAY);
//        }
    }
}
