package Moonlight.mod.ability.base;

import Moonlight.mod.CultivationMod;
import Moonlight.mod.ability.CultivationAbilities;
import Moonlight.mod.ability.MenuType;
import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import Moonlight.mod.util.RotationUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public abstract class Ability {
    public enum ActivationType {
        INSTANT,
        TOGGLED,
        CHANNELED
    }

    public enum Status {
        FAILURE,
        UNUSABLE,
        SUCCESS,
        COOLDOWN,
        EMPTYINV,
        DISABLE,
        QI
    }

    public enum Classification {
        NONE
    }

    public static double getPower(Ability ability, LivingEntity owner) {
        IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getAbilityPower();
    }

    public double getPower(LivingEntity owner) {
        return getPower(this, owner);
    }

    public void cooldown(LivingEntity owner) {
        IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
        if (this.getRealCooldown(owner) > 0) {
            cap.addCooldown(this);
        }
    }

    public boolean isScalable(LivingEntity owner) {
        return this.getActivationType(owner) != ActivationType.TOGGLED;
    }

    public boolean isInternalArt() {
        return false;
    }

    public boolean canDisable() {
        return true;
    }

    public boolean isUnlocked(LivingEntity owner) {
        IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
        return cap.isUnlocked(this);
    }

    public boolean canUnlock(LivingEntity owner) {
        if (this.isBlocked(owner)) return false;
        if (owner instanceof Player player && player.getAbilities().instabuild) return true;

        return true;
    }

    public boolean isBlocked(LivingEntity owner) {
        Ability parent = this.getParent(owner);
        return parent != null && !parent.isUnlocked(owner);
    }

    @Nullable
    public Ability getParent(LivingEntity owner) {
        return null;
    }

    public Classification getClassification() {
        return Classification.NONE;
    }

    public boolean isMelee() {
        return false;
    }

    public boolean usesHands() {
        return true;
    }

    public abstract boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target);
    public abstract ActivationType getActivationType(LivingEntity owner);
    public abstract void run(LivingEntity owner);

    public List<Ability> getRequirements() {
        return List.of();
    }

    protected int getCooldown() {
        return 0;
    }

    public int getRealCooldown(LivingEntity owner) {
        IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();

        return this.getCooldown();
    }

    public MenuType getMenuType() {
        return MenuType.NONE;
    }

    public boolean isValid(LivingEntity owner) {
        if (owner == null || (owner instanceof Player player && player.isSpectator())) return false;
        if (!this.isUnlocked(owner)) return false;

        if (!owner.getCapability(DataHandler.INSTANCE).isPresent()) return false;
        IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();

        if (!(owner instanceof Player player)) return true;
        if (this.canDisable() && cap.hasDisable()) {
            return false;
        }
        if (cap.isFatigued()) {
            return false;
        }

        for (Ability ability : this.getRequirements()) {
            if (!ability.isUnlocked(owner)) return false;
        }
        return true;
    }

    public void addDuration(LivingEntity owner) {
        IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
        if (this instanceof IDurationable durationable && durationable.getRealDuration(owner) > 0) {
            cap.addDuration(this);
        }
    }

    public void charge(LivingEntity owner) {
        IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
        if (!(owner instanceof Player player && player.getAbilities().instabuild)) {
            cap.useQi((double) this.getRealCost(owner));

            if (this.getRealCooldown(owner) > 0) {
                cap.addCooldown(this);
            }
        }
    }

    public Status getStatus(LivingEntity owner) {
        IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
        if (!(owner instanceof Player player && (player.getAbilities().instabuild))) {
            if (cap.isFatigued()) {
                return Status.FAILURE;
            }
            if (this.canDisable() && cap.hasDisable()) {
                return Status.DISABLE;
            }
            if (!cap.isCooldownDone(this)) {
                return Status.COOLDOWN;
            }
            if (!this.checkCost(owner)) {
                return Status.QI;
            }
        }
        return Status.SUCCESS;
    }

    public Status isTriggerable(LivingEntity owner) {
        if (!CultivationAbilities.getAbilities(owner).contains(this)) return Status.UNUSABLE;
        if (this instanceof IAttack || this instanceof ICharged) {
            return this.getStatus(owner);
        }

        Status status = this.getStatus(owner);
        if (status == Status.SUCCESS || status == Status.COOLDOWN) {
            this.charge(owner);
        }
        return status;
    }

    public Status isStillUsable(LivingEntity owner) {
        if (!CultivationAbilities.getAbilities(owner).contains(this)) return Status.UNUSABLE;

        if (this instanceof IAttack || this instanceof ICharged) {
            return this.getStatus(owner);
        }

        Status status = this.getStatus(owner);
        if (status == Ability.Status.SUCCESS || status == Ability.Status.COOLDOWN) {
            this.charge(owner);
        }
        return status;
    }

    public boolean checkCost(LivingEntity owner) {
        IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
        if (!(owner instanceof Player player && player.getAbilities().instabuild)) {
            float cost = this.getRealCost(owner);
            return cap.getCurrentQi() >= cost;
        }
        return true;
    }

    public Component getName() {
        ResourceLocation key = CultivationAbilities.getKey(this);
        return Component.translatable(String.format("ability.%s.%s", key.getNamespace(), key.getPath()));
    }

    public abstract float getCost(LivingEntity owner);

    public boolean shouldLog(LivingEntity owner) {
        return this.getActivationType(owner) == ActivationType.TOGGLED;
    }

    public Component getEnableMessage() {
        ResourceLocation key = CultivationAbilities.getKey(this);
        if (key == null) return Component.empty();

        return Component.translatable(String.format("ability.%s.%s.enable", key.getNamespace(), key.getPath()));
    }

    public Component getDisableMessage() {
        ResourceLocation key = CultivationAbilities.getKey(this);
        if (key == null) return Component.empty();

        return Component.translatable(String.format("ability.%s.%s.disable", key.getNamespace(), key.getPath()));
    }

    public @Nullable BlockHitResult getBlockHit(LivingEntity owner, double range) {
        Vec3 start = owner.getEyePosition();
        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        Vec3 end = start.add(look.scale(range));
        HitResult result = RotationUtil.getHitResult(owner, start, end);

        if (result.getType() == HitResult.Type.BLOCK) {
            return (BlockHitResult) result;
        } else if (result.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) result).getEntity();
            Vec3 offset = entity.position().subtract(0.0D, 5.0D, 0.0D);
            return owner.level().clip(new ClipContext(entity.position(), offset, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
        }
        return null;
    }

    public float getRealCost(LivingEntity owner, float cost) {
        if (!owner.getCapability(DataHandler.INSTANCE).isPresent()) return cost;
        IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
        float output = cap.getOutput();

        if (output > 0) {
            if (output >= 1.0F) {
                output = Mth.clamp(output * 0.8F, 1.0F, 100.0F);
                cost *= (this.isScalable(owner) ? output : 1.0F);
            } else {
                float scaledOutput = Mth.clamp(output, 0.01F, 1.0F);
                float costMultiplier = (float) Math.pow(scaledOutput, 0.5);
                cost *= (this.isScalable(owner) ? (costMultiplier) : 1.0F);
            }
        }
        return Float.parseFloat(String.format(Locale.ROOT, "%.2f", cost));
    }

    public float getRealCost(LivingEntity owner) {
        return this.getRealCost(owner, this.getCost(owner));
    }

    public interface IDurationable {
        default int getDuration() {
            return 0;
        }

        default int getRealDuration(LivingEntity owner) {
            int duration = this.getDuration();
            if (duration > 0) {
                duration = (int) (duration * 12);
            }

            return duration;
        }
    }

    public interface IChannelened {
        default void onStart(LivingEntity owner) {}
        default void onStop(LivingEntity owner) {}

        default int getCharge(LivingEntity owner) {
            IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
            return cap.getCharge((Ability) this);
        }
    }

    public interface ICharged extends IChannelened {
        default boolean onRelease(LivingEntity owner) {
            return true;
        }
    }

    public interface IToggled {
        void onEnabled(LivingEntity owner);
        void onDisabled(LivingEntity owner);

        default void applyModifiers(LivingEntity owner) {}
        default void removeModifiers(LivingEntity owner) {}
    }

    public interface IAttack {
        boolean attack(DamageSource source, LivingEntity owner, LivingEntity target);
    }
}
