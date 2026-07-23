package Moonlight.mod.items.armor.wudangSectLeader;

import Moonlight.mod.CultivationMod;
import Moonlight.mod.items.armor.CustomArmorItem;
import Moonlight.mod.items.armor.ModArmorMaterials;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import javax.annotation.Nullable;

public class wudangSectLeaderChestplate extends CustomArmorItem {
    public wudangSectLeaderChestplate(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(ModArmorMaterials.CLOTH, Type.CHESTPLATE, new Properties().stacksTo(1).rarity(Rarity.EPIC));
        this.miscItemInformation = "The chestplate of the esteemed sect leader of the Wudang Sect.";
    }

    @Override
    @Nullable
    public String getArmorTexture(ItemStack itemStack, Entity entity, EquipmentSlot equipmentSlot, String string) {
        return String.format("%s:textures/armor/wudangSectLeader/wudangSectLeaderChestplate.png", CultivationMod.MODID);
    }
}
