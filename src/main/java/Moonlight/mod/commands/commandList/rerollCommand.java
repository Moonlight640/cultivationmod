package Moonlight.mod.commands.commandList;

import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import Moonlight.mod.data.capability.PlayerData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class rerollCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("cultivationReroll")
                .requires((player) -> player.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.entity()).executes((ctx) ->
                        rerollCMD(EntityArgument.getPlayer(ctx, "player")))));
    }

    public static int rerollCMD(ServerPlayer player) {
        IPlayerData cap = player.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();

        cap.generate(player);

        return 1;
    }
}
