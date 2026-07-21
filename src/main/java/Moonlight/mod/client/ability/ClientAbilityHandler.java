package Moonlight.mod.client.ability;

import Moonlight.mod.CultivationMod;
import Moonlight.mod.ability.AbilityHandler;
import Moonlight.mod.ability.AbilityTriggerEvent;
import Moonlight.mod.ability.CultivationAbilities;
import Moonlight.mod.ability.base.Ability;
import Moonlight.mod.ability.misc.QiFlow;
import Moonlight.mod.client.CultivationKeys;
import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import Moonlight.mod.entity.base.IJumpInputListener;
import Moonlight.mod.entity.base.IRightClickInputListener;
import Moonlight.mod.network.PacketHandler;
import Moonlight.mod.network.packets.c2s.*;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class ClientAbilityHandler {
    private static @Nullable Ability channeled;
    private static @Nullable KeyMapping current;
    private static boolean isChanneling;
    private static boolean isRightDown;
    private static final Map<KeyMapping, Ability> activeChannels = new HashMap<>();
    private static final Map<KeyMapping, Boolean> isChannelingMap = new HashMap<>();

    @Mod.EventBusSubscriber(modid = CultivationMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientAbilityHandlerForgeEvents {
        @SubscribeEvent
        public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
            //LivingEntity entity = event.getEntity();

            if (!(event.getRenderer().getModel() instanceof PlayerModel<?> playerModel)) return;
        }

        private static void channel(@Nullable Ability ability, @Nullable KeyMapping key) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null) return;

            channeled = ability;
            current = key;
            isChanneling = false;
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null) return;

            if (current != null && channeled != null) {
                boolean isHeld = current.isDown();

                if (isHeld) {
                    if (!isChanneling) {
                        //AbilityHandler.trigger(minecraft.player, channeled);
                        PacketHandler.sendToServer(new TriggerAbilityC2S(CultivationAbilities.getKey(channeled)));
                    }
                    isChanneling = true;
                } else if (isChanneling) {
                    AbilityHandler.untrigger(minecraft.player, channeled);
                    PacketHandler.sendToServer(new UntriggerAbilityC2S(CultivationAbilities.getKey(channeled)));

                    channel(null, null);
                }
            }

            if (minecraft.player.getVehicle() instanceof IRightClickInputListener listener) {
                if (!isRightDown && minecraft.mouseHandler.isRightPressed()) {
                    listener.setDown(true);
                    PacketHandler.sendToServer(new RightClickInputListenerC2S(true));
                    isRightDown = true;
                } else if (isRightDown && !minecraft.mouseHandler.isRightPressed()) {
                    listener.setDown(false);
                    PacketHandler.sendToServer(new RightClickInputListenerC2S(false));
                    isRightDown = false;
                }
            }
        }

        private static void handleInput(int inputObj, int action) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null) return;

            if (inputObj == GLFW.GLFW_KEY_SPACE) {
                if (action == InputConstants.PRESS || action == InputConstants.RELEASE) {
                    boolean down = action == InputConstants.PRESS;

                    if (minecraft.player.getVehicle() instanceof IJumpInputListener listener) {
                        listener.setJump(down);
                        PacketHandler.sendToServer(new JumpInputListenerC2S(down));
                    } else if (minecraft.player.getFirstPassenger() instanceof IJumpInputListener listener) {
                        listener.setJump(down);
                        PacketHandler.sendToServer(new JumpInputListenerC2S(down));
                    }
                }
            }

            if (action == InputConstants.PRESS) {
//                if (CultivationKeys.ACTIVATE_ABILITY.isDown()) {
//                    Ability ability = AbilityOverlay.getSelected();
//
//                    if (ability != null) {
//                        if (ability.getActivationType(minecraft.player) == Ability.ActivationType.CHANNELED) {
//                            if (channeled == null) {
//                                channel(ability, CultivationKeys.ACTIVATE_ABILITY);
//                            }
//                        } else if (CultivationKeys.ACTIVATE_ABILITY.consumeClick()) {
//                            PacketHandler.sendToServer(new TriggerAbilityC2S(CultivationAbilities.getKey(ability)));
//                        }
//                    }
//                }

                if (CultivationKeys.OPEN_CULTIVATION_MENU.consumeClick()) {
                    PacketHandler.sendToServer(new GetPlayerDataC2S());
                    //minecraft.setScreen(new CultivationScreen());
                }

                if (CultivationKeys.INCREASE_OUTPUT.consumeClick()) {
                    IPlayerData cap = minecraft.player.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
                    PacketHandler.sendToServer(new IncreaseOutputC2S());
                    cap.increaseOutput();
                }

                if (CultivationKeys.DECREASE_OUTPUT.consumeClick()) {
                    IPlayerData cap = minecraft.player.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
                    PacketHandler.sendToServer(new DecreaseOutputC2S());
                    cap.decreaseOutput();
                }

                if (CultivationKeys.ACTIVATE_QI_SHIELD.isDown()) {
                    if (channeled == null) {
                        channel(CultivationAbilities.QI_SHIELD.get(), CultivationKeys.ACTIVATE_QI_SHIELD);
                    }
                }

                if (CultivationKeys.ACTIVATE_QI_FLOW.consumeClick()) {
                    PacketHandler.sendToServer(new TriggerAbilityC2S(CultivationAbilities.getKey(CultivationAbilities.QI_FLOW.get())));
                }

                if (CultivationKeys.ACTIVATE_INFINITY.consumeClick()) {
                    PacketHandler.sendToServer(new TriggerAbilityC2S(CultivationAbilities.getKey(CultivationAbilities.INFINITY.get())));
                }

                if (CultivationKeys.TOGGLE_MEDITATION.consumeClick()) {
                    IPlayerData cap = minecraft.player.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();

                    if (!cap.getInternalArtName().equals("No Manual")) {
                        PacketHandler.sendToServer(new ToggleMeditatingC2S());
                        cap.setMeditating(!cap.isMeditating());
                    } else {
                        minecraft.player.sendSystemMessage(Component.literal("Player has not learnt any manual. Therefore are unable to meditate"));
                    }
                }
            } else if (action == InputConstants.RELEASE) {
//                if ((inputObj == CultivationKeys.OPEN_CULTIVATION_MENU.getKey().getValue() && minecraft.screen instanceof CultivationScreen)) {
//                    minecraft.screen.onClose();
//                }
            }
        }

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            handleInput(event.getKey(), event.getAction());
        }

        @SubscribeEvent
        public static void onMouseInput(InputEvent.MouseButton event) {
            handleInput(event.getButton(), event.getAction());
        }
    }

    public static Ability.Status trigger(Ability ability) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer owner = minecraft.player;

        if (owner == null) return Ability.Status.FAILURE;

        if (!owner.getCapability(DataHandler.INSTANCE).isPresent()) return Ability.Status.FAILURE;
        IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();

        if (ability.getActivationType(owner) == Ability.ActivationType.INSTANT) {
            ability.charge(owner);
            ability.addDuration(owner);
            MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
            ability.run(owner);
            MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
        } else if (ability.getActivationType(owner) == Ability.ActivationType.TOGGLED) {
            ability.addDuration(owner);
            MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
            cap.toggle(ability);
            MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
        } else if (ability.getActivationType(owner) == Ability.ActivationType.CHANNELED) {
            ability.addDuration(owner);
            MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
            cap.channel(ability);
            MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
        }
        return Ability.Status.SUCCESS;
    }
}
