package Moonlight.mod.ability.misc;

import Moonlight.mod.CultivationMod;
import Moonlight.mod.ability.CultivationAbilities;
import Moonlight.mod.ability.base.Ability;
import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import Moonlight.mod.util.CultivationUtil;
import Moonlight.mod.util.EntityUtil;
import Moonlight.mod.util.HelperMethods;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QiFlow extends Ability implements Ability.IToggled {
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("641b629b-f7b7-4066-a486-8e1d670a7439");
    private static final UUID PROJECTION_STEP_HEIGHT_UUID = UUID.fromString("df3957ac-ad26-432a-a26e-711aab5dead5");

    private static final double SPEED = 0.03D;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return true;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public boolean usesHands() {
        return false;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!owner.level().getBlockState(owner.blockPosition()).getFluidState().isEmpty()) {
            Vec3 movement = owner.getDeltaMovement();

            if (movement.y < 0.0D) {
                double amount = 0.01D;
                if (owner.isInWater()) {
                    amount = 0.15D;
                }
                owner.setDeltaMovement(movement.x, amount, movement.z);
            }
            owner.setOnGround(true);
        }

        if (owner instanceof Player player) {
            float f = 0.0F;

            if (owner.onGround() && !owner.isDeadOrDying() && !owner.isSwimming()) {
                f = Math.min(0.1F, (float) owner.getDeltaMovement().horizontalDistance());
            }
            player.bob += (f - player.bob) * 0.4F;
        }

//        if (!(owner.level() instanceof ServerLevel level)) return;
//        IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
//
    }

    @Override
    public void applyModifiers(LivingEntity owner) {
        double newSpeed = SPEED;
        double maxSpeed = 4.0F;

        IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
        double ratio = cap.getCurrentQi() / cap.getMaxQi();

        EntityUtil.applyModifier(owner, ForgeMod.STEP_HEIGHT_ADDITION.get(), PROJECTION_STEP_HEIGHT_UUID, "Step height addition", 2.0F, AttributeModifier.Operation.ADDITION);
        EntityUtil.applyArmorBoost(owner);

        if (!(cap.isChanneling(CultivationAbilities.QI_SHIELD.get()) && owner instanceof Player player)) {
            if (ratio <= 0.5 && ratio > 0.3) {
                newSpeed *= 0.85;
                maxSpeed *= 0.85;
            }
            if (ratio <= 0.3 && ratio > 0.15) {
                newSpeed *=0.75;
                maxSpeed *= 0.75;
            }

            if (ratio <= 0.15) {
                newSpeed *= 0.65;
                maxSpeed *= 0.65;
            }
            if (cap.isFatigued()) {
                newSpeed *= 0.75;
                maxSpeed *= 0.75;
            }

            EntityUtil.applyModifier(owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID, "Movement speed",
                    Math.min(maxSpeed, newSpeed * this.getPower(owner)), AttributeModifier.Operation.ADDITION);
        }
    }

    @Override
    public void removeModifiers(LivingEntity owner) {
        EntityUtil.removeModifier(owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID);
        EntityUtil.removeModifier(owner, ForgeMod.STEP_HEIGHT_ADDITION.get(), PROJECTION_STEP_HEIGHT_UUID);
        EntityUtil.removeArmorBoost(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        //IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();

        return 0.1F;
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        return true;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Override
    public Status isStillUsable(LivingEntity owner) {
        IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getCurrentQi() == 0.0F ? Status.FAILURE : super.isStillUsable(owner);
    }

    @Mod.EventBusSubscriber(modid = CultivationMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class QiFlowForgeEvents {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onLivingHurt(LivingHurtEvent event) {
            DamageSource source = event.getSource();
            if (!(source.getEntity() instanceof LivingEntity attacker)) return;
            if (attacker.level().isClientSide) return;

            LivingEntity victim = event.getEntity();

            if (CultivationAbilities.hasToggled(attacker, CultivationAbilities.QI_FLOW.get())) {
                if (attacker.getCapability(DataHandler.INSTANCE).isPresent()) {
                    IPlayerData attackerCap = attacker.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();

                    if (HelperMethods.isMelee(source)) {
                        double increase = 0.75F * CultivationUtil.getPower((attackerCap.getBodyExperience() + attackerCap.getQiExperience()));

                        if (!(attacker instanceof Player player) || !player.getAbilities().instabuild) {
                            //
                        }

                        event.setAmount(event.getAmount() * (float) increase); // Was + changed for testing whether i want it multiplier or not.
                    }
                }
            }

            if (source.is(DamageTypeTags.BYPASSES_ARMOR)) return;

            if (CultivationAbilities.hasToggled(victim, CultivationAbilities.QI_FLOW.get())) {
                if (victim.getCapability(DataHandler.INSTANCE).isPresent()) {
                    IPlayerData victimCap = victim.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();

                    float armor = !source.is(DamageTypeTags.BYPASSES_SHIELD) && victimCap.isChanneling(CultivationAbilities.QI_SHIELD.get()) ? 2.5F : 1.2F;
                    float block = event.getAmount() / armor;

                    event.setAmount(block);
                }
            }
        }
    }
}
