package Moonlight.mod.items.armor;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class CustomArmorItem extends ArmorItem {
    public String miscItemInformation;
    private String colorCode;

    public CustomArmorItem(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        super.use(level, player, hand);
        if (level.isClientSide)
            return InteractionResultHolder.fail(player.getItemInHand(hand));

        // Possible give of advancement?
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> tooltip, TooltipFlag tooltipFlag) {
        switch (this.getRarity(itemStack)) {
            case COMMON:
                colorCode = "§7";
                break;
            case UNCOMMON:
                colorCode = "§2";
                break;
            case RARE:
                colorCode = "§6";
                break;
            case EPIC:
                colorCode = "§5";
                break;
        }
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable(colorCode + this.miscItemInformation));
        } else {
            tooltip.add(Component.translatable(colorCode + "Hold " + "§eSHIFT " + colorCode + "for more information!"));
        }
        super.appendHoverText(itemStack, level, tooltip, tooltipFlag);
    }
}
