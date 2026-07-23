package Moonlight.mod.items.armor.wudangSectLeader;

import Moonlight.mod.CultivationMod;
import Moonlight.mod.items.armor.CustomArmorItem;
import Moonlight.mod.items.armor.ModArmorMaterials;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class wudangSectLeaderBoots extends CustomArmorItem {
    public wudangSectLeaderBoots(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(ModArmorMaterials.CLOTH, Type.BOOTS, new Properties().stacksTo(1).rarity(Rarity.EPIC));
        this.miscItemInformation = "The boots of the esteemed sect leader of the Wudang Sect.";
    }

    @Override
    @Nullable
    public String getArmorTexture(ItemStack itemStack, Entity entity, EquipmentSlot equipmentSlot, String string) {
        return String.format("%s:textures/armor/wudangSectLeader/wudangSectLeaderBoots.png", CultivationMod.MODID);
    }
}
