package Moonlight.mod.items.custom;

import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import Moonlight.mod.data.capability.playerStats.InternalArtData;
import Moonlight.mod.manual.ManualList;
import Moonlight.mod.network.PacketHandler;
import Moonlight.mod.network.packets.s2c.SyncPlayerDataS2C;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ManualItem extends Item {
    private final InternalArtData manual;

    public ManualItem(Properties pProperties, InternalArtData manualData) {
        super(pProperties);
        this.manual = manualData;
    }

    public InternalArtData getManual() { return manual; }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            player.sendSystemMessage(Component.literal(this.manual.getManualData().getDescription()));
            IPlayerData cap = player.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();

            //TODO: Do compatibility checks and so forth
            cap.setInternalArt(manual);
            PacketHandler.sendToClient(new SyncPlayerDataS2C(cap.serializeNBT()), (ServerPlayer) player);

            if (!player.isCreative()) {
                itemStack.shrink(1);
            }
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}
