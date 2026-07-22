package Moonlight.mod.util;

import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EntityUtil {
    private static final UUID QI_ARMOR_UUID = UUID.fromString("c7b8f3f0-9c4f-4e76-b69f-dc2f3d94e7b8");
    private static final UUID QI_ARMOR_TOUGHNESS_UUID = UUID.fromString("f3a3c0e2-bc9a-45a2-9219-0f1f6de65c17");

    @Nullable
    public static LivingEntity getOwner(TamableAnimal tamableAnimal) {
        LivingEntity owner = tamableAnimal;

        while (owner instanceof TamableAnimal parent && parent.isTame()) {
            owner = parent.getOwner();

            if (owner == null) return null;
        }

        return owner;
    }

    public static <T extends Entity> List<T> getTargetableEntities(Class<T> tClass, EntityGetter entityGetter, @Nullable LivingEntity owner, AABB boundingBox) {
        return entityGetter.getEntitiesOfClass(tClass, boundingBox, EntitySelector.ENTITY_STILL_ALIVE
                .and(EntitySelector.NO_CREATIVE_OR_SPECTATOR)
                .and(entity -> owner == null || (entity != owner && (!(entity instanceof TamableAnimal tamableAnimal) || getOwner(tamableAnimal) != owner))));

    }

    public static void applyArmorBoost(LivingEntity owner) {
        if (!(owner instanceof Player)) return;

        IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();

        removeModifier(owner, Attributes.ARMOR, QI_ARMOR_UUID);
        removeModifier(owner, Attributes.ARMOR_TOUGHNESS, QI_ARMOR_TOUGHNESS_UUID);

        double currentArmor = owner.getAttributeValue(Attributes.ARMOR);
        double currentToughness = owner.getAttributeValue(Attributes.ARMOR_TOUGHNESS);

        double qiArmorBonus = (cap.getQiExperience() + cap.getBodyExperience()) / 5000.0f;
        double qiArmorToughnessBonus = ((cap.getQiExperience() + cap.getBodyExperience()) / 5000.0f) * 0.5;

        qiArmorBonus *= cap.getOutput();
        qiArmorToughnessBonus *= cap.getOutput();

        if (cap.isFatigued()) {
            qiArmorBonus *= 0.75;
            qiArmorToughnessBonus *= 0.75;
        }

        double maxArmor = 40;
        double maxToughness = maxArmor * 0.65;

        if (currentArmor + qiArmorBonus > maxArmor) {
            qiArmorBonus = Math.max(0, maxArmor - currentArmor);
        }
        if (currentToughness + qiArmorToughnessBonus > maxToughness) {
            qiArmorToughnessBonus = Math.max(0, maxToughness - currentToughness);
        }

        applyModifier(owner, Attributes.ARMOR, QI_ARMOR_UUID, "Qi Armor Bonus", qiArmorBonus, AttributeModifier.Operation.ADDITION);
        applyModifier(owner, Attributes.ARMOR_TOUGHNESS, QI_ARMOR_TOUGHNESS_UUID, "Qi Armor Toughness Bonus", qiArmorToughnessBonus, AttributeModifier.Operation.ADDITION);
    }

    public static void removeArmorBoost(LivingEntity owner) {
        removeModifier(owner, Attributes.ARMOR, QI_ARMOR_UUID);
        removeModifier(owner, Attributes.ARMOR_TOUGHNESS, QI_ARMOR_TOUGHNESS_UUID);
    }

    public static void rotation(Entity entity, Vec3 look) {
        double d0 = look.horizontalDistance();
        entity.setYRot((float) (Mth.atan2(look.x, look.z) * (double) (180.0F / (float) Math.PI)));
        entity.setXRot((float) (Mth.atan2(look.y, d0) * (double) (180.0F / (float) Math.PI)));
        entity.yRotO = entity.getYRot();
        entity.xRotO = entity.getXRot();
    }

    public static void offset(Entity entity, Vec3 look, Vec3 pos) {
        rotation(entity, look);
        entity.setPos(pos.x, pos.y, pos.z);
    }

    public static boolean applyModifier(LivingEntity owner, Attribute attribute, UUID identifier, String name, double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = owner.getAttribute(attribute);
        AttributeModifier modifier = new AttributeModifier(identifier, name, amount, operation);

        if (instance != null) {
            AttributeModifier existing = instance.getModifier(identifier);

            if (existing != null) {
                if (existing.getAmount() != amount) {
                    instance.removeModifier(identifier);
                    instance.addTransientModifier(modifier);
                    return true;
                }
            } else {
                instance.addTransientModifier(modifier);
                return true;
            }
        }
        return false;
    }

    public static void removeModifier(LivingEntity owner, Attribute attribute, UUID identifier) {
        AttributeInstance instance = owner.getAttribute(attribute);

        if (instance != null) {
            instance.removeModifier(identifier);
        }
    }
}
