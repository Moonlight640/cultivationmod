package Moonlight.mod.items.custom;

import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import Moonlight.mod.data.capability.playerStats.DelayedTickEvents.Actions.StopConsumingPillAction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PillItem extends Item {
    public static final FoodProperties PILL = (new FoodProperties.Builder().meat().alwaysEat().build());

    private final float qiAmountGainMultiplier;
    private final float qiExperienceGainMultiplier;
    private final float bodyExperienceGainMultiplier;
    private final int timeTakenToConsume;
    private final boolean save;

    public PillItem(Properties pProperties, float qiAmountGainMultiplier, float qiExperienceGainMultiplier, float bodyExperienceGainMultiplier, int StimeTakenToConsume, boolean save) {
        super(pProperties);

        this.qiAmountGainMultiplier = qiAmountGainMultiplier;
        this.qiExperienceGainMultiplier = qiExperienceGainMultiplier;
        this.bodyExperienceGainMultiplier = bodyExperienceGainMultiplier;
        this.timeTakenToConsume = (StimeTakenToConsume * 20);
        this.save = save;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        ItemStack result = super.finishUsingItem(pStack, pLevel, pLivingEntity);

        if (!pLevel.isClientSide && pLivingEntity instanceof Player player) {
            IPlayerData cap = player.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();


            cap.setConsumingPill(true);

            cap.delayTickEvent(
                    new StopConsumingPillAction(player.getUUID()),
                    this.timeTakenToConsume,
                    this.save
            );
        }

        return result;
    }
}
