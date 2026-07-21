package Moonlight.mod.commands.commandList;

import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import Moonlight.mod.data.capability.playerStats.TalentGrade;
import Moonlight.mod.network.PacketHandler;
import Moonlight.mod.network.packets.s2c.SyncPlayerDataS2C;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.command.EnumArgument;

public class TalentCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("cultivationTrait")
                .requires((player) -> player.hasPermission(2))
                .then(Commands.literal("set").then(Commands.argument("player", EntityArgument.entity()).then(Commands.argument("talent", EnumArgument.enumArgument(TalentGrade.class)).executes((ctx) ->
                        setTalent(EntityArgument.getPlayer(ctx, "player"), ctx.getArgument("talent", TalentGrade.class)))))));
    }   //TODO: Make it so I can add which type of stat the talent goes to

    public static int setTalent(ServerPlayer player, TalentGrade talentGrade) {
        IPlayerData cap = player.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
        //TODO: Set the talent
        PacketHandler.sendToClient(new SyncPlayerDataS2C(cap.serializeNBT()), player);
        return 1;
    }
}
